variable "aws_region" {
  description = "The AWS region to deploy resources in."
  type        = string
  default     = "us-east-1"
}

variable "domain_name" {
  description = "The root domain name."
  type        = string
  default     = "me-perdi.com"
}

variable "subdomain" {
  description = "The subdomain for the API."
  type        = string
  default     = "api.fireworks"
}

variable "hosted_zone_id" {
  description = "The AWS Hosted Zone ID for the root domain."
  type        = string
  default     = "Z0472943134E5AS4HZPIV"
}

variable "lambda_image_uri" {
  description = "The URI of the Docker image in ECR to deploy."
  type        = string
}
