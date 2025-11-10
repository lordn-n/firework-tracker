# --- AWS ECR Data Source ---
# This looks up the existing ECR repository you created manually.
data "aws_ecr_repository" "fireworks_api_repo" {
  name = "fireworks-api" # The name of your existing ECR repository
}

# --- AWS Secrets Manager ---
data "aws_secretsmanager_secret" "db_credentials" {
  arn = "arn:aws:secretsmanager:us-east-1:871997720004:secret:fireworks-tracker-ah5zj8"
}

data "aws_secretsmanager_secret_version" "db_credentials_version" {
  secret_id = data.aws_secretsmanager_secret.db_credentials.id
}

# --- IAM Role and Policy for Lambda ---
resource "aws_iam_role" "lambda_exec_role" {
  name = "fireworks-api-lambda-exec-role"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_policy" "secrets_manager_read_policy" {
  name        = "fireworks-api-secrets-manager-read-policy"
  description = "Allows Lambda function to read the database credentials from Secrets Manager."

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [{
      Action   = "secretsmanager:GetSecretValue"
      Effect   = "Allow"
      Resource = data.aws_secretsmanager_secret.db_credentials.arn
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_secrets_read" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = aws_iam_policy.secrets_manager_read_policy.arn
}

# --- Lambda Function (from Docker image) ---
resource "aws_lambda_function" "fireworks_api_lambda" {
  function_name = "fireworks-api-function"
  role          = aws_iam_role.lambda_exec_role.arn
  package_type  = "Image"
  
  image_uri = var.lambda_image_uri

  environment {
    variables = {
      DATABASE_URL = jsondecode(data.aws_secretsmanager_secret_version.db_credentials_version.secret_string)["DATABASE_URL"]
    }
  }

  timeout = 30
}

# --- API Gateway, Custom Domain, and DNS (unchanged) ---
# ... (rest of the file remains the same)
resource "aws_apigatewayv2_api" "fireworks_api" {
  name          = "fireworks-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id           = aws_apigatewayv2_api.fireworks_api.id
  integration_type = "AWS_PROXY"
  integration_uri  = aws_lambda_function.fireworks_api_lambda.invoke_arn
}

resource "aws_apigatewayv2_route" "api_proxy_route" {
  api_id    = aws_apigatewayv2_api.fireworks_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.fireworks_api.id
  name        = "$default"
  auto_deploy = true
}

resource "aws_lambda_permission" "api_gateway_permission" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.fireworks_api_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.fireworks_api.execution_arn}/*/*"
}

resource "aws_acm_certificate" "api_cert" {
  domain_name       = "${var.subdomain}.${var.domain_name}"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "cert_validation_record" {
  for_each = {
    for dvo in aws_acm_certificate.api_cert.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = var.hosted_zone_id
}

resource "aws_acm_certificate_validation" "cert_validation" {
  certificate_arn         = aws_acm_certificate.api_cert.arn
  validation_record_fqdns = [for record in aws_route53_record.cert_validation_record : record.fqdn]
}

resource "aws_apigatewayv2_domain_name" "api_domain" {
  domain_name = "${var.subdomain}.${var.domain_name}"

  domain_name_configuration {
    certificate_arn = aws_acm_certificate_validation.cert_validation.certificate_arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}

resource "aws_apigatewayv2_api_mapping" "api_mapping" {
  api_id      = aws_apigatewayv2_api.fireworks_api.id
  domain_name = aws_apigatewayv2_domain_name.api_domain.id
  stage       = aws_apigatewayv2_stage.default_stage.id
}

resource "aws_route53_record" "api_alias_record" {
  name    = aws_apigatewayv2_domain_name.api_domain.domain_name
  type    = "A"
  zone_id = var.hosted_zone_id

  alias {
    name                   = aws_apigatewayv2_domain_name.api_domain.domain_name_configuration[0].target_domain_name
    zone_id                = aws_apigatewayv2_domain_name.api_domain.domain_name_configuration[0].hosted_zone_id
    evaluate_target_health = false
  }
}
