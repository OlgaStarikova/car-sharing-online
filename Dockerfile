# Builder stage
FROM openjdk:17-jdk-alpine as builder
WORKDIR /car-sharing-online
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} car-sharing-online.jar
RUN java -Djarmode=layertools -jar car-sharing-online.jar extract

# Final stage
FROM openjdk:17-jdk-alpine
WORKDIR /car-sharing-online
COPY --from=builder car-sharing-online/dependencies/ ./
COPY --from=builder car-sharing-online/spring-boot-loader/ ./
COPY --from=builder car-sharing-online/snapshot-dependencies/ ./
COPY --from=builder car-sharing-online/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
