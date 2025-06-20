# CI/CD for ipu-user-service

This README.md explains the steps that are taken during the CI/CD pipeline for `ipu-user-service`

## CI

Ansible runs application tests in `cicd/ansible/ci.yaml`

- Creates a Docker Image

- Creates a Helm Chart

- Pushes Docker Image to `advancedpricing.azurecr.io`

- Pushes Helm Chart to `advancedpricing.azurecr.io/helm`

- //TODO Setup unit tests to be run in Docker and published in AzDO

## CD

Ansible runs deployment using `cicd/ansible/cd.yaml`

- Pulls Helm Chart from registry

- Deploys Helm Chart using `Helm upgrade --install` command to AKS Cluster
