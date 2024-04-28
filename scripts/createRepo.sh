#!/bin/bash

if [ -z "$ECR_REPO" ] || [ -z "$AWS_REGION" ] || [ -z "$PROJECT" ]
  then
    echo "Error: missing neccessary argument"
    exit 1
fi
ECR_ARN=$(aws ecr create-repository --repository-name ${ECR_REPO} --image-tag-mutability IMMUTABLE --image-scanning-configuration scanOnPush=true --region ${AWS_REGION}|jq -r '.repository .repositoryArn')
if [ ! -z ${ECR_ARN} ];
then
    aws ecr set-repository-policy \
    --repository-name ${ECR_REPO} \
    --policy-text file://ecr-permissions-policy.json \
    --region ${AWS_REGION} && {
        aws ecr put-lifecycle-policy \
        --repository-name ${ECR_REPO} \
        --lifecycle-policy-text file://ecr-lifecycle-policy.json \
        --region ${AWS_REGION} && {
            aws ecr tag-resource \
            --resource-arn ${ECR_ARN} \
            --region ${AWS_REGION} \
            --tags Key=Name,Value=${ECR_REPO} Key=Owner,Value=${PROJECT} Key=Type,Value=backend
        } || exit 1
    } || exit 1
fi 
exit 0