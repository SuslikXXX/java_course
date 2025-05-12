# Task Management System

Spring Boot приложение для управления задачами, пользователями и уведомлениями с использованием PostgreSQL.

## Возможности

- Регистрация и аутентификация пользователей
- Управление задачами (создание, просмотр, мягкое удаление)
- Система уведомлений
- Хранение данных в PostgreSQL

## Быстрый старт

1. Клонируйте репозиторий:
   ```sh
   git clone https://github.com/SuslikXXX/task-management-system.git
   cd task-management-system
   ```

2. Создайте базу данных и пользователя в PostgreSQL:
   ```sh
   psql -U postgres -c "CREATE DATABASE taskdb;"
   psql -U postgres -c "CREATE USER taskuser WITH PASSWORD 'taskpass';"
   psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskuser;"
   ```

3. Проверьте настройки подключения в `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
   spring.datasource.username=taskuser
   spring.datasource.password=taskpass
   ```

4. Соберите и запустите приложение:
   ```sh
   ./mvnw clean install
   java -jar target/demo-0.0.1-SNAPSHOT.jar
   ```

5. API будет доступен по адресу: [http://localhost:8081](http://localhost:8081)

## Примеры запросов

### Регистрация пользователя
```sh
curl -X POST -H "Content-Type: application/json" \
-d '{"username":"testuser", "email":"test@example.com", "password":"password123"}' \
http://localhost:8081/api/users/register
```

### Вход пользователя
```sh
curl -X POST -H "Content-Type: application/json" \
-d '{"username":"testuser", "password":"password123"}' \
http://localhost:8081/api/users/login
```

### Создание задачи
```sh
curl -X POST -H "Content-Type: application/json" \
-d '{"title":"Test Task", "description":"This is a test task", "targetDate":"2025-05-10T12:00:00", "user":{"id":1}}' \
http://localhost:8081/api/tasks
```

### Получение всех задач пользователя
```sh
curl http://localhost:8081/api/tasks/user/1
```

### Создание уведомления
```sh
curl -X POST -H "Content-Type: application/json" \
-d '{"message":"Test notification", "user":{"id":1}}' \
http://localhost:8081/api/notifications
```

### Получение всех уведомлений пользователя
```sh
curl http://localhost:8081/api/notifications/user/1
```

## Структура проекта

- `User`, `Task`, `Notification` — основные сущности
- REST API для управления пользователями, задачами и уведомлениями
- Используется только PostgreSQL, H2 полностью удалён

## Лицензия

MIT 