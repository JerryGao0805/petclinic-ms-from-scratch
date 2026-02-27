     FROM eclipse-temurin:21-jre-jammy

     WORKDIR /app
     COPY target/*.jar /app/app.jar

     EXPOSE 8081
     ENTRYPOINT ["java","-jar","/app/app.jar"]