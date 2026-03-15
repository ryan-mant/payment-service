#!/bin/bash

BUCKET_NAME="terraform-state-bucket"
TABLE_NAME="terraform-state-lock"
AWS_ENDPOINT_URL="http://localstack:4566"

echo "--- Checking and creating S3 bucket: ${BUCKET_NAME} ---"
aws s3api head-bucket --bucket "${BUCKET_NAME}" --endpoint-url "${AWS_ENDPOINT_URL}" 2>/dev/null
if [ $? -ne 0 ]; then
  aws s3api create-bucket --bucket "${BUCKET_NAME}" --region us-east-1 --endpoint-url "${AWS_ENDPOINT_URL}"
  echo "Bucket created."
else
  echo "Bucket already exists."
fi

echo "--- Checking and creating DynamoDB table: ${TABLE_NAME} ---"
aws dynamodb describe-table --table-name "${TABLE_NAME}" --endpoint-url "${AWS_ENDPOINT_URL}" 2>/dev/null
if [ $? -ne 0 ]; then
  aws dynamodb create-table \
    --table-name "${TABLE_NAME}" \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url "${AWS_ENDPOINT_URL}"
  echo "Table created."
else
  echo "Table already exists."
fi

echo "--- Backend ready to use ---"
