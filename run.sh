#!/bin/bash

# Exit on any error
set -e

# Build function
build() {
  echo "Building the application..."
  mvn clean package -DskipTests
  echo "Build completed successfully!"
}

# Run locally function
run_local() {
  echo "Running the application locally..."
  mvn spring-boot:run
}

# Docker build function
docker_build() {
  echo "Building Docker image..."
  docker build -t laundry-management-system:latest .
  echo "Docker image built successfully!"
}

# Docker compose function
docker_compose_up() {
  echo "Starting application with Docker Compose..."
  docker-compose up -d
  echo "Application started! Access it at http://localhost:8080"
  echo "Swagger UI available at http://localhost:8080/swagger-ui.html"
}

# Docker compose down function
docker_compose_down() {
  echo "Stopping application and containers..."
  docker-compose down
  echo "Application stopped!"
}

# Display usage information
usage() {
  echo "Usage: $0 [command]"
  echo "Commands:"
  echo "  build         - Build the application"
  echo "  run           - Run the application locally"
  echo "  docker-build  - Build Docker image"
  echo "  docker-start  - Start application with Docker Compose"
  echo "  docker-stop   - Stop application and containers"
  echo "  help          - Display this help message"
}

# Process command line arguments
case "$1" in
  build)
    build
    ;;
  run)
    run_local
    ;;
  docker-build)
    docker_build
    ;;
  docker-start)
    docker_compose_up
    ;;
  docker-stop)
    docker_compose_down
    ;;
  help)
    usage
    ;;
  *)
    echo "Unknown command: $1"
    usage
    exit 1
    ;;
esac

exit 0