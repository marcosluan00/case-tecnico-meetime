   # Utiliza uma imagem base oficial do Java
   FROM eclipse-temurin:17-jdk-alpine

   # Define o diretório de trabalho no contêiner
   WORKDIR /app

   # Copia o arquivo jar gerado pelo Maven para o contêiner
   COPY target/integracao-0.0.1-SNAPSHOT.jar app.jar

   # Expõe a porta 8080 (onde a aplicação será executada)
   EXPOSE 8080

   # Define o comando para rodar a aplicação
   ENTRYPOINT ["java", "-jar", "app.jar"]