# Task Management System

Spring Boot приложение для управления задачами, пользователями и уведомлениями.

## Возможности

* Регистрация и аутентификация пользователей
* Управление задачами (создание, просмотр, удаление)
* Система уведомлений
* In-memory хранение данных

## Быстрый старт

1. Клонируйте репозиторий:
```bash
git clone https://github.com/SuslikXXX/java_course_work.git
cd java_course_work
```

2. Соберите и запустите приложение:
```bash
./mvnw clean install
mvn spring-boot:run
```

3. API будет доступен по адресу: http://localhost:8081

## Примеры запросов

### Регистрация пользователя
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"username":"testuser", "email":"test@example.com", "password":"password123"}' \
http://localhost:8081/api/users/register
```

### Вход пользователя
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"username":"testuser", "password":"password123"}' \
http://localhost:8081/api/users/login
```

### Создание задачи
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"title":"Test Task", "description":"This is a test task", "user":{"id":1}}' \
http://localhost:8081/api/tasks
```

### Получение всех задач пользователя
```bash
curl http://localhost:8081/api/tasks/user/1
```

### Создание уведомления
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"message":"Test notification", "user":{"id":1}}' \
http://localhost:8081/api/notifications
```

### Получение всех уведомлений пользователя
```bash
curl http://localhost:8081/api/notifications/user/1
```

## Структура проекта

* `User`, `Task`, `Notification` — основные сущности
* REST API для управления пользователями, задачами и уведомлениями
* In-memory хранение данных

## Лицензия

MIT 