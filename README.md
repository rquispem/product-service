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

Checking Resilience4j

```

1. Get an access token
unset ACCESS_TOKEN
ACCESS_TOKEN=$(curl -k https://writer:secret@localhost:8443/oauth/token -d grant_type=password -d username=magnus -d password=password -s | jq -r .access_token)
echo $ACCESS_TOKEN

2. Check the get api, should get 200 code
curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/2 -w "%{http_code}\n" -o /dev/null -s

3. Check circuitbraker state, should be closed
docker run --rm -it --network=my-network alpine wget product-composite:8080/actuator/health -qO - | jq -r .components.circuitBreakers.details.product.details.state

4. Make 3 negative’s tests

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/2?delay=3 -s | jq .


5. Test again within waitDurationInOpenState time


6. Wrap this up by listing the last three state transitions using the following command:

docker run --rm -it --network=my-network alpine wget product-composite:8080/actuator/circuitbreakerevents/product/STATE_TRANSITION -qO - | jq -r '.circuitBreakerEvents[-3].stateTransition, .circuitBreakerEvents[-2].stateTransition, .circuitBreakerEvents[-1].stateTransition

7. Force a random error to retry

time curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/2?faultPercent=25 -w "%{http_code}\n" -o /dev/null -s


8. Show retries

docker run --rm -it --network=my-network alpine wget product-composite:8080/actuator/retryevents -qO - | jq '.retryEvents[-2], .retryEvents[-1]'

```

Running test in Kubernetes
```
Unset KUBECONFIG
minikube start \
--memory=10240 \
--cpus=4 \
--disk-size=30g \
--vm-driver=virtualbox \
--kubernetes-version=v1.15.5 \
--profile=product-project

kubectl create namespace <namespace_name>
kubectl config set-context $(kubectl config current-context) --namespace=<namespace_name>
eval $(minikube -p <profile> docker-env)

kubectl create secret generic config-server-secrets \
  --from-literal=ENCRYPT_KEY=my-very-secure-encrypt-key \
  --from-literal=SPRING_SECURITY_USER_NAME=dev-usr \
  --from-literal=SPRING_SECURITY_USER_PASSWORD=dev-pwd \
  --save-config

kubectl create secret generic config-client-credentials \
--from-literal=CONFIG_SERVER_USR=dev-usr \
--from-literal=CONFIG_SERVER_PWD=dev-pwd \ 
--save-config

docker pull mysql:5.7
docker pull mongo:3.6.9
docker pull rabbitmq:3.7.8-management
docker pull openzipkin/zipkin:2.21

kubectl apply -k kubernetes/services/overlays/dev

kubectl wait --timeout=600s --for=condition=ready pod --all


HOST=$(minikube -p <profile_name> ip) PORT=31443 ./test-em-all-kubernetes.bash
```