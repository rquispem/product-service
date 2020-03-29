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

### Running persistence tests
```
./gradlew microservices:product-service:test --tests PersistenceTests
```

### Checking data stored in DB
```
docker-compose exec mongodb mongo product-db --quiet --eval "db.products.find()"
docker-compose exec mongodb mongo recommendation-db --quiet --eval "db.recommendations.find()"
docker-compose exec mysql mysql -uuser -p review-db -e "select * from reviews"
```