# Task tracker microservice

## Введение
Микросервис реализует базовые методы для управления проектами со списком задач
## Настройки проекта
1. Настройка подключения к базе данных

   ```
    url: jdbc:postgresql://${POSTGRES_SERVER}:${POSTGRES_PORT}/${POSTGRES_DB}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
   ```
2. Конфигурация подключения к Kafka

   ```
    bootstrap-servers: ${$KAFKA_SERVER}
   ```
3. Конифгурация топика отправки email уведомлений

   ```
     ${KAFKA_EMAIL_TOPIC}
   ```

Упрвлением зависимостей и сборки занимается gradle. Подробнее в файле [build.gradle.kts](https://github.com/Merantory/task-tracker-api/blob/main/build.gradle.kts)

## Запуск приложения
Запуск осуществляется через <code>docker-compose up -d</code>
