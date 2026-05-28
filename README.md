# MyApp

This project is a sample Spring Boot service that:

- stores orders in MySQL
- uploads order payloads to S3
- publishes events to SNS
- queues asynchronous work to SQS
- reads sensitive configuration from AWS Secrets Manager using AWSpring
- builds and deploys to EKS through GitHub Actions and ECR

## Key decisions

- `Spring Boot 3.5` keeps the app on the current Boot 3 line.
- `AWSpring 3.4` is used for AWS integration and auto-configuration.
- `MySQL` is used for realistic local and cloud parity.
- `LocalStack` gives you local AWS service emulation without needing real AWS during development.
- `GitHub OIDC` is used in CI/CD so you do not need long-lived AWS keys in GitHub secrets.

## Project layout

- `.github/workflows/ci-cd.yml`: build, push to ECR, deploy to EKS
- `docker-compose.yml`: local MySQL and LocalStack
- `k8s/base`: shared Kubernetes manifests
- `k8s/overlays/dev`: dev namespace and deployment patch
- `k8s/overlays/staging`: staging namespace and deployment patch
- `src/main/java`: Spring Boot application code

## Branch to environment mapping

- Push to `dev` deploys to the `dev` EKS environment
- Push to `release` deploys to the `staging` EKS environment
- Pull requests targeting either branch run validation only

## Local run

Start dependencies:

```bash
docker compose up -d
```

Run the app:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Create a sample order:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Ada","productCode":"BOOK-1","quantity":2}'
```

## Secrets Manager setup

The app imports two optional secrets:

- `/myapp/common`
- `/myapp/<APP_ENV>`

The local bootstrap script creates `/myapp/common` and `/myapp/local`.

In AWS, create secrets with keys like:

```json
{
  "spring.datasource.username": "myapp_user",
  "spring.datasource.password": "super-secret",
  "app.aws.s3.bucket-name": "myapp-dev-bucket",
  "app.aws.sns.order-topic-arn": "arn:aws:sns:us-east-1:123456789012:myapp-order-events",
  "app.aws.sqs.order-queue": "myapp-order-queue"
}
```

## GitHub Actions setup

Create these GitHub repository variables:

- `AWS_REGION`
- `ECR_REPOSITORY`
- `EKS_CLUSTER_NAME_DEV`
- `EKS_CLUSTER_NAME_STAGING`
- `K8S_NAMESPACE_DEV`
- `K8S_NAMESPACE_STAGING`

Create these GitHub repository variables or replace them directly in the workflow:

- `AWS_ROLE_ARN_DEV`
- `AWS_ROLE_ARN_STAGING`

The roles should trust GitHub OIDC and allow:

- ECR push
- EKS describe and cluster auth
- any other deployment support actions your cluster needs

## EKS runtime permissions

The application pod should use IAM Roles for Service Accounts. Replace the placeholder role ARNs in:

- `k8s/overlays/dev/serviceaccount-patch.yaml`
- `k8s/overlays/staging/serviceaccount-patch.yaml`

That IAM role should allow:

- `s3:PutObject`
- `s3:GetObject`
- `sns:Publish`
- `sqs:SendMessage`
- `sqs:ReceiveMessage`
- `sqs:DeleteMessage`
- `secretsmanager:GetSecretValue`

## CI/CD flow

1. GitHub Actions builds the jar and Docker image.
2. The image is pushed to ECR with the commit SHA tag and an environment tag.
3. The workflow updates kubeconfig for the correct EKS cluster.
4. Kubernetes manifests are applied with the correct overlay.
5. The deployment image is updated to the new ECR tag.
6. The workflow waits for rollout success.

## Next things you may want to add

- Flyway migrations
- Helm instead of Kustomize
- environment-specific ingress
- integration tests with Testcontainers
- Argo CD or Flux if you want GitOps later

