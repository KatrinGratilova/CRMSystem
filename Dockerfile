# Используем официальный образ JDK
FROM openjdk:17-jdk-slim

# Рабочая директория
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/CRMSystem-0.0.1-SNAPSHOT.jar app.jar

# Запуск приложения
CMD ["java", "-jar", "app.jar"]
