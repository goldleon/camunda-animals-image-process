# Getting Started

## Running the Application

### Docker Compose Method

#### Setting up the Environment Variables

Before you start the application, you need to set up your environment variables.
These contain the connection info to your Camunda SaaS Cluster.
This can be done by creating a `.env` file in the root directory of the project.

Here's an example of what your `.env` file should look like:

```env
CAMUNDA_SAAS_REGION=your_region
CAMUNDA_SAAS_CLIENT_ID=your_client_id
CAMUNDA_SAAS_CLIENT_SECRET=your_client_secret
CAMUNDA_SAAS_CLUSTER_ID=your_cluster_id
PORT=8080
DATABASE_URL=mongodb-url
```

#### Running the application

```
docker compose up -d
```

This starts the Spring Boot client (port 8080) and the MongoDB instance to store the pictures.

### Helm Method

#### Setting up the Environment Variables

Before you start the application, you need to set up the environment variables.
These contain the connection info to your Camunda SaaS Cluster.
This can be done by replacing the placeholders in the [configmap file](./helm/templates/home-rido-test-camunda-animal-picture-app--env-configmap.yaml) with your values.

#### Deploying the Application

```
helm install camunda-animal-picture-app ./helm
```

This will deploy the application to your Kubernetes cluster.
