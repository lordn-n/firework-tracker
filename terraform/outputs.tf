output "api_endpoint" {
  description = "The default invoke URL for the API Gateway."
  value       = aws_apigatewayv2_stage.default_stage.invoke_url
}

output "custom_domain_url" {
  description = "The custom domain URL for the API."
  value       = "https://${aws_apigatewayv2_domain_name.api_domain.domain_name}"
}
