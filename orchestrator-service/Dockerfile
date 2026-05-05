FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /project
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
WORKDIR /deployments
ENV LANGUAGE=en_US:en
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
COPY --from=build /project/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /project/target/quarkus-app/*.jar /deployments/
COPY --from=build /project/target/quarkus-app/app/ /deployments/app/
COPY --from=build /project/target/quarkus-app/quarkus/ /deployments/quarkus/
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS_APPEND -jar /deployments/quarkus-run.jar"]
