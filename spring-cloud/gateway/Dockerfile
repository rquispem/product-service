FROM openjdk:12.0.2
EXPOSE 8080
ADD ./build/libs/*.jar app.jar
# “We will specify the command to be used by Docker when a container is started up”
ENTRYPOINT ["java", "-jar", "/app.jar"]