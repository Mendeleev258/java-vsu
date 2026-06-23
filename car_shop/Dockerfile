# Этап 1: Сборка с Gradle
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Копируем gradle файлы
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY gradlew /app/

# Загружаем зависимости
RUN ./gradlew dependencies --no-daemon || true

# Копируем исходный код и собираем
COPY src /app/src
RUN ./gradlew build --no-daemon -x test

# Этап 2: Запуск с Eclipse Temurin (JDK 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR
COPY --from=build /app/build/libs/*.jar app.jar

# Создаём пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]