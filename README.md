#  Trip Planner

## О проекте

**Trip Planner** — внутренний корпоративный веб-сервис для планирования командировок сотрудников компании.

Сервис помогает сотрудникам:
- выбрать офис назначения
- найти подходящий отель из корпоративного списка
- изучить инфраструктуру рядом с офисом или отелем (кафе, рестораны, достопримечательности, парки, тимбилдинг-локации)
- сохранять интересные места в список "Хочу посетить"
- оставлять отзывы и оценки

Цель проекта — упростить подготовку к командировкам, предоставив всю необходимую информацию в одном месте.

---

##  Функциональность

###  Геолокация и офисы
- Фиксированный список городов компании
- В каждом городе один или несколько офисов (lat/lon)
- Все объекты имеют координаты
- Расчёт расстояний между объектами:
  - формула Хаверсина
  - OSRM (OpenStreetMap)
- Сортировка объектов по расстоянию

---

###  Отели
- Корпоративный список отелей
- Просмотр инфраструктуры рядом с выбранным отелем

---

###  Wishlist
- Добавление объектов в список "Хочу посетить"
- Управление списком

---

###  Отзывы и оценки
- Оценка объектов (1–5)
- Комментарии
- Средний рейтинг на карточках
- Админ может просматривать и удалять отзывы

---

###  Администрирование
- Добавление и редактирование городов
- Управление офисами
- Управление отелями
- Просмотр отзывов с фильтрацией

---
##  Роли пользователей

###  admin
- Управление городами
- Управление офисами
- Управление отелями
- Просмотр и удаление отзывов

###  user
- Просмотр объектов
- Фильтрация по типу и расстоянию
- Wishlist
- Добавление новых мест
- Оставление отзывов

---

##  Технологический стек

### Backend
- Java 17+
- Spring Boot 3.x
- Spring Security + JWT

### Database
- PostgreSQL
- Flyway 

### API Documentation
- Swagger / OpenAPI 3

### Testing
- JUnit 5
- Mockito
- TestContainers

### Геолокация
- OpenStreetMap (Nominatim / OSRM)
- Формула Хаверсина

### Frontend
- React Admin

### DevOps
- Docker
- Docker Compose

---

##  Запуск проекта

###  Требования

Перед запуском убедитесь, что установлены:
- Docker
- Docker Compose

Проверка:

```bash
docker -v
docker compose version
```

### Переменные окружения

Создайте файл .env в корне проекта:
```text
POSTGRES_DB=trip_planner
DB_URL=jdbc:postgresql://db:5432/trip_planner
DB_USER=postgres
DB_PASSWORD=1234
VITE_API_URL=http://localhost:8080
```

### Запуск проекта
Собрать и запустить все сервисы:
```
docker compose up --build
```

После запуска:
- Backend API: http://localhost:8080
- React Admin: http://localhost:3000
- PostgreSQL: localhost:5432

---

## Структура проекта

```text
.
├── admin/                       # React Admin frontend
│   ├── src/
│   │   ├── app/                 # app shell, providers, theme
│   │   ├── features/            # feature-level logic
│   │   ├── resources/           # React Admin resources
│   │   ├── shared/              # shared API/config helpers
│   │   └── widgets/             # reusable UI blocks
│   ├── Dockerfile
│   └── nginx.conf
├── backend/                     # Java Spring Boot backend
│   ├── src/main/java/com/tripplanner/backend/
│   │   ├── application/         # use cases and services
│   │   ├── config/              # Spring, security, CORS, OpenAPI config
│   │   ├── domain/              # domain model and business rules
│   │   ├── infrastructure/      # persistence, security, external adapters
│   │   ├── shared/              # shared primitives and helpers
│   │   └── web/                 # controllers and HTTP layer
│   ├── src/main/resources/db/migration/
│   └── Dockerfile
├── docs/                        # API documentation
├── docker-compose.yml
└── .env.example
```

