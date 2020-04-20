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

### Working with rabbit mq and kafka
```
curl localhost:8080/actuator/health -s | jq .
curl -s localhost:8080/actuator/health | jq -r .status
```
```
export COMPOSE_FILE=docker-compose-partitions.yml
docker-compose build && docker-compose up -d
unset COMPOSE_FILE
```

```
export COMPOSE_FILE=docker-compose-kafka.yml
docker-compose build && docker-compose up -d
```

To see a list of topics
```
docker-compose exec kafka /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper --list
```
To see the partitions in a specific topic
```
docker-compose exec kafka /opt/kafka/bin/kafka-topics.sh --describe --zookeeper zookeeper --topic products
```
To see all the messages in a specific topic, for example, the products topic
```
docker-compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic products --from-beginning --timeout-ms 1000
```

To see all the messages in a specific partition
```
docker-compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic products --from-beginning --timeout-ms 1000 --partition 0
```

Use automated tests
```
export COMPOSE_FILE=docker-compose-partitions.yml
sh test-em-all.bash start stop
unset COMPOSE_FILE
```

Interact with eureka server
```
sh test-em-all.bash start
docker-compose up -d --scale review=3
docker-compose logs -f review

docker-compose up -d --scale review=2 --scale eureka=0
docker-compose up -d --scale review=1 --scale eureka=0
docker-compose up -d --scale review=1 --scale eureka=0 --scale product=2
curl localhost:8080/product-composite/2 -s | jq -r .serviceAddresses.pro

//extract the addresses of the
curl localhost:8080/product-composite/2 -s | jq -r .serviceAddresses

//Ask the composite product service for the IP addresses it finds for the review
docker-compose exec product-composite getent hosts review

docker-compose exec --index=1 review cat /etc/hosts
docker-compose exec --index=2 review cat /etc/hosts

curl localhost:8080/product-composite/2 -s | jq -r .serviceAddresses.rev

curl localhost:8080/product-composite/2 -m 2. Wait 2 seconds timeout
```

Acquiring access token Using implicit  grant flow
```
//get access token for reader using browser
https://localhost:8443/oauth/authorize?response_type=token&client_id=reader&redirect_uri=http://my.redirect.uri

//get access token for writer using browser
https://localhost:8443/oauth/authorize?response_type=token&client_id=writer&redirect_uri=http://my.redirect.uri
```

Acquiring access token Using code  grant flow(most secure)

```
//1. Obtener el código de acceso q solo servirá una vez
https://localhost:8443/oauth/authorize?response_type=code&client_id=reader&redirect_uri=http://my.redirect.uri&scope=product:read&state=35725


//2. Intercambiar el código por el access token
curl -k https://reader:secret@localhost:8443/oauth/token \
 -d grant_type=authorization_code \
 -d client_id=reader \
 -d redirect_uri=http://my.redirect.uri \
 -d code=$CODE -s | jq .

curl -k https://reader:secret@localhost:8443/oauth/token \
 -d grant_type=authorization_code \
 -d client_id=reader \
 -d redirect_uri=http://my.redirect.uri \
 -d code=95K1Hu -s | jq .

//Get code for writer client
https://localhost:8443/oauth/authorize?response_type=code&client_id=writer&redirect_uri=http://my.redirect.uri&scope=product:read+product:write&state=72489

curl -k https://writer:secret@localhost:8443/oauth/token \
 -d grant_type=authorization_code \
 -d client_id=writer \
 -d redirect_uri=http://my.redirect.uri \
 -d code=qnTz2J -s | jq .

// Calling protected apis
//1. Get access token using one of the access token acquired for reader
ACCESS_TOKEN=an-invalid-token

2//
curl https://localhost:8443/product-composite/2 -k -H "Authorization: Bearer $ACCESS_TOKEN" -i


//3 we need to call delete request with a writer scope so first we get one for it
curl https://localhost:8443/product-composite/999 -k -H "Authorization: Bearer $ACCESS_TOKEN" -X DELETE -i
``` 