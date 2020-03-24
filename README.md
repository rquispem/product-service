# PRODUCT MICROSERVICES
Product-repo is a multi project initiative for microservice learning with java and spring boot.

### Running project
It uses docker and docker compose

```shell script
./gradlew :microservices:product-service:build

docker-compose build //build the images
docker-compose up -d  //start containers
docker-compose down   //stop cotainers
```

### Swagger Doc
```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v2/api-docs
```