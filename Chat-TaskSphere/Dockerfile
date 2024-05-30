# Establece la imagen base
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo en /app
WORKDIR /app

# Copia el archivo .jar desde la ubicación local al contenedor en /app
COPY Chatwebsockets.jar /app/Chatwebsockets.jar

# Expone el puerto 8080 para que la aplicación sea accesible
EXPOSE 8080

# Comando para ejecutar la aplicación cuando se inicie el contenedor
CMD ["java", "-jar", "Chatwebsockets.jar"]
