# Документация backend-сервиса Medical Applied
---

## 1. Назначение сервиса

Backend-сервис Medical Applied — это REST API для медицинского справочного приложения. Сервис предоставляет каталог лекарственных препаратов, поиск и фильтрацию, карточки препаратов, регистрацию и авторизацию пользователей, профиль здоровья, историю просмотров, email-верификацию и AI-рекомендации на основе профиля пользователя и списка препаратов.

---

## 2. Краткое описание системы

Система Medical Applied в текущей реализации состоит из следующих backend-блоков:

1. **Каталог препаратов** — таблица `ma_medicals`, read-only REST API, фильтрация по стране, категории и названию.
2. **Пользователи** — таблица `ma_users`, регистрация, обновление, удаление, смена пароля.
3. **Авторизация** — Spring Security + JWT, роли `USER` и `ADMIN`.
4. **Профиль здоровья** — таблица `ma_user_health_profiles`, хранение веса, хронических заболеваний, особенностей здоровья и аллергий.
5. **История просмотров** — таблица `ma_medical_views_history`, сохранение открытых карточек препаратов.
6. **Email-верификация** — таблица `ma_email_verifications`, UUID-токены, HTML-письмо, scheduler отправки.
7. **AI-модуль** — интеграция с Gemini API через `RestClient`, prompt-файлы в `resources/prompts`.
8. **SEO endpoint** — генерация строки ключевых слов из статичных значений и названий препаратов.

---

## 3. Технологический стек

| Компонент | Технология                       | Назначение                                               |
|---|----------------------------------|----------------------------------------------------------|
| Язык | Java 25                          | Основной язык проекта                                    |
| Framework | Spring Boot 4.0.3                | Основа backend-приложения                                |
| REST | Spring Boot Starter WebMVC       | HTTP API                                                 |
| Security | Spring Security                  | Защита endpoints, роли, JWT-фильтр                       |
| JWT | JJWT 0.13.0                      | Генерация и проверка access token                        |
| ORM | Spring Data JPA                  | Работа с сущностями и БД                                 |
| JDBC | Spring Data JDBC starter         | Подключён в `pom.xml`, но основной доступ идёт через JPA |
| БД | PostgreSQL                       | Основное хранилище данных                                |
| Миграции | Liquibase                        | Создание таблиц и начальное заполнение                   |
| Кэш | Spring Data Redis                | Кэш каталога и справочников                              |
| DTO validation | Jakarta Bean Validation          | Валидация request DTO                                    |
| Mapper | MapStruct 1.6.3                  | Маппинг Entity/DTO                                       |
| Boilerplate | Lombok 1.18.42                   | Getter/setter/constructor генерация                      |
| API docs | Springdoc OpenAPI 3.0.2          | Swagger UI и OpenAPI schema                              |
| Email | Spring Boot Starter Mail         | SMTP-отправка писем                                      |
| AI | Gemini API + Spring `RestClient` | Генерация рекомендаций и ответов                         |
| Scheduler | Spring Scheduling                | Отправка email-заявок и очистка истории                  |
| Сборка | Maven Wrapper 3.9.9              | Сборка и запуск                                          |
| Docker | Docker                           | Запуск `redis` контейнера                                    |
| Тесты | Зависимости подключены           | `src/test` в архиве отсутствует                          |

---

## 4. Архитектура приложения

Приложение реализовано как слоистый монолит.

```text
Client / Frontend
        |
        v
REST Controllers
        |
        v
Services
        |
        +-------------------+
        |                   |
        v                   v
Repositories           External integrations
        |                   |
        v                   +--> Gemini API
PostgreSQL                 +--> SMTP server
        ^
        |
Liquibase migrations

Redis используется сервисным слоем как cache layer.
```

Архитектурные слои:

| Слой | Пакеты | Назначение |
|---|---|---|
| Controller layer | `rest`, `rest.impl` | REST endpoints и Swagger-аннотации |
| Service layer | `service`, `service.impl` | Бизнес-логика |
| Repository layer | `repository`, `repository.medical`, `repository.redis` | Работа с PostgreSQL и Redis |
| DTO layer | `dto`, `dto.ai` | Контракты API и Gemini API |
| Entity layer | `model.user`, `model.medical`, `model.profile` | JPA-сущности |
| Mapper layer | `model.mapping` | MapStruct-маппинг |
| Configuration layer | `config`, `config.ai`, `config.auditaware`, `config.security` | Конфигурация приложения |
| Security layer | `config.security`, `util.auth` | JWT, фильтр, UserDetails, CORS |

---

## 5. Структура проекта

```text
medical-applied-master
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
└── src/main
    ├── java/tender/ma/medicalapplied
    │   ├── MedicalAppliedApplication.java
    │   ├── config
    │   │   ├── RedisConfig.java
    │   │   ├── SpringdocConfiguration.java
    │   │   ├── ai
    │   │   ├── auditaware
    │   │   └── security
    │   ├── dto
    │   │   └── ai
    │   ├── exceptions
    │   │   └── error
    │   ├── model
    │   │   ├── mapping
    │   │   ├── medical
    │   │   ├── profile
    │   │   └── user
    │   ├── repository
    │   │   ├── medical
    │   │   └── redis
    │   ├── rest
    │   │   └── impl
    │   ├── service
    │   │   ├── client
    │   │   │   └── impl
    │   │   ├── impl
    │   │   └── schedulers
    │   └── util
    │       └── auth
    └── resources
        ├── application.yml
        ├── banner.txt
        ├── db
        │   ├── db.changelog-master.xml
        │   ├── 0.0.1
        │   ├── 0.1.0
        │   └── 0.1.1
        ├── email-verification-format-page.html
        └── prompts
            ├── answers-prompt.txt
            └── recommendations-prompt.txt
```

---

## 6. Основные доменные сущности

### 6.1. User

Класс: `tender.ma.medicalapplied.model.user.User`  
Таблица: `ma_users`

Назначение: пользователь системы. Сущность реализует `UserDetails`, поэтому используется Spring Security как principal.

Основные поля:

| Поле | Тип | Назначение |
|---|---|---|
| `id` | `UUID` | Идентификатор |
| `firstName` | `String` | Имя |
| `lastName` | `String` | Фамилия |
| `birthDate` | `LocalDate` | Дата рождения |
| `gender` | `GenderTypes` | `MALE` или `FEMALE` |
| `role` | `Role` | Роль пользователя |
| `email` | `String` | Email, используется как username |
| `emailVerified` | `boolean` | Подтверждение email |
| `passwordHash` | `String` | BCrypt-хэш пароля |
| `userConsent` | `boolean` | Согласие с пользовательским соглашением |
| `privacyConsent` | `boolean` | Согласие с политикой конфиденциальности |
| audit-поля | `String`/`LocalDateTime` | `createdBy`, `createdDate`, `modifiedBy`, `modifiedDate` |

Связи:

- `ManyToOne` с `Role` через `role_id`;
- логически `OneToOne` с `UserHealthProfile`;
- логически `OneToMany` с `MedicalViewHistory`;
- логически `OneToMany` с `EmailVerification`.

Особенности:

- `getAuthorities()` возвращает `ROLE_` + имя роли;
- аккаунт всегда считается активным, не истёкшим и не заблокированным.

### 6.2. Role

Класс: `model.user.Role`  
Таблица: `ma_roles`

Поля:

| Поле | Тип | Назначение |
|---|---|---|
| `id` | `UUID` | Идентификатор роли |
| `name` | `RoleTypes` | `ADMIN` или `USER` |
| `description` | `String` | Описание роли |
| audit-поля | | Данные аудита |

Миграция создаёт две роли: `ADMIN` и `USER`.

### 6.3. Medical

Класс: `model.medical.Medical`  
Таблица: `ma_medicals`

Назначение: лекарственный препарат в каталоге.

Поля:

| Поле | Назначение |
|---|---|
| `id` | UUID препарата |
| `countryEn` | Страна на английском |
| `countryRu` | Страна на русском |
| `type` | Категория препарата |
| `name` | Название препарата |
| `activeIngredient` | Действующее вещество |
| `description` | Описание |
| `indications` | Показания |
| `contraindications` | Противопоказания |
| `dosing` | Дозировка/способ применения |
| `kidneyFriendly` | Допустимость при проблемах с почками |
| `pregnantFriendly` | Допустимость при беременности |
| `breastfedFriendly` | Допустимость при грудном вскармливании |
| `liverFriendly` | Допустимость при проблемах с печенью |
| `childFriendly` | Допустимость для детей |
| `stomachFriendly` | Щадящий для желудка |

В seed-скрипте найдено 76 препаратов, страна фактически только `Russia` / `Россия`.

### 6.4. UserHealthProfile

Класс: `model.profile.UserHealthProfile`  
Таблица: `ma_user_health_profiles`

Назначение: медицинский профиль пользователя для персонализации AI-ответов.

Поля:

| Поле | Тип |
|---|---|
| `id` | `UUID` |
| `user` | `User` |
| `weight` | `Double` |
| `chronicConditions` | `List<String>` / `jsonb` |
| `healthFeatures` | `List<String>` / `jsonb` |
| `allergies` | `List<String>` / `jsonb` |
| audit-поля | audit |

Связь: `OneToOne` с пользователем, в БД `user_id UUID UNIQUE`.

### 6.5. MedicalViewHistory

Класс: `model.profile.MedicalViewHistory`  
Таблица: `ma_medical_views_history`

Поля:

| Поле | Назначение |
|---|---|
| `id` | ID записи истории |
| `user` | Пользователь |
| `medical` | Просмотренный препарат |
| `viewedAt` | Дата просмотра |

Запись создаётся при открытии карточки препарата `GET /api/medicals/{id}`.

### 6.6. EmailVerification

Класс: `model.profile.EmailVerification`  
Таблица: `ma_email_verifications`

Поля:

| Поле | Назначение |
|---|---|
| `id` | ID запроса |
| `user` | Пользователь |
| `expired` | Срок действия |
| `status` | Статус заявки |
| `verificationToken` | UUID-токен подтверждения |

Статусы: `FAILED`, `UNVERIFIED`, `CREATED`, `PROCESSING`, `SENT`, `VERIFIED`.

---

## 7. REST API

### Общие правила доступа

| Endpoint                                             | Доступ |
|------------------------------------------------------|---|
| `POST /api/login`                                    | Публичный |
| `POST /api/users`                                    | Публичный |
| `/swagger-ui.html`, `/swagger-ui/**`, `/api-docs/**` | Публичный |
| `/api/users/*/verify`                                | Публичный |
| Все остальные endpoints                              | Требуют JWT |
| `GET /api/users`                                     | Требует JWT и роль `ADMIN` |

JWT передаётся так:

```http
Authorization: Bearer <accessToken>
```

### 7.1. Auth API

Base URL: `/api`

#### POST `/api/login`

Назначение: авторизация пользователя и получение JWT.

Авторизация: не требуется.

Request body:

```json
{
  "email": "mail@gmail.com",
  "password": "password"
}
```

Response body:

```json
{
  "accessToken": "jwt-token"
}
```

Логика:

- поиск пользователя по email;
- проверка пароля через BCrypt;
- генерация JWT через `JwtUtil`;
- при ошибке — `401 Unauthorized`.

#### POST `/api/logout`

Назначение: выход из системы.

Авторизация: требуется.

Response: `204 No Content`.

Фактическая реализация: метод `AuthServiceImpl.logout()`. JWT инвалидируется. + Frontend очищает cache с заголовком Authorization

### 7.2. Users API

Base URL: `/api/users`

#### GET `/api/users`

Назначение: получить всех пользователей.

Авторизация: требуется. Роль: `ADMIN`.

Response: `List<UserDto>`.

#### GET `/api/users/{id}`

Назначение: получить пользователя по UUID.

Авторизация: требуется.

Response: `UserDto`.

Проблема: нет проверки, что пользователь запрашивает себя, и нет ограничения только для администратора.

#### POST `/api/users`

Назначение: регистрация пользователя.

Авторизация: не требуется.

Request body:

```json
{
  "firstName": "Ivan",
  "lastName": "Ivanov",
  "birthDate": "2000-01-01",
  "gender": "MALE",
  "email": "user@gmail.com",
  "userConsent": true,
  "privacyConsent": true,
  "password": "pass1"
}
```

Response: `UserDto`.

Логика:

- проверка уникальности email;
- хэширование пароля BCrypt;
- назначение роли `USER`;
- сохранение в `ma_users`.

#### POST `/api/users/{id}`

Назначение: обновить пользователя.

Авторизация: требуется.

Фактический метод — `POST`, хотя семантически больше подходит `PUT` или `PATCH`.

Request body: `UserRequestDto`.

Особенность: если email изменился, `emailVerified` сбрасывается в `false`.

Проблема: нет owner-check.

#### DELETE `/api/users/{id}`

Назначение: удалить пользователя.

Авторизация: требуется.

Response: удалённый `UserDto`.

Проблема: нет owner-check/role-check. Также удаление может конфликтовать с FK-зависимостями.

#### POST `/api/users/{id}/reset-password`

Назначение: смена пароля.

Авторизация: требуется.

Request body:

```json
{
  "oldPassword": "old1",
  "newPassword": "new1"
}
```

Логика:

- ищет пользователя по path id;
- проверяет старый пароль;
- сохраняет новый BCrypt-хэш.

Проблема: нет проверки, что пользователь меняет пароль себе.

### 7.3. Health Profile API

#### POST `/api/users/{id}/health`

Назначение: создать профиль здоровья.

Request body:

```json
{
  "weight": 72.5,
  "chronicConditions": ["гастрит"],
  "healthFeatures": ["чувствительный желудок"],
  "allergies": ["ибупрофен"]
}
```

Response: `UserHealthProfileDto`.

Логика: один пользователь может иметь только один профиль.

#### PUT `/api/users/{id}/health/{healthId}`

Назначение: обновить профиль здоровья.

Логика: проверяется, что профиль принадлежит пользователю.

#### GET `/api/users/{id}/health`

Назначение: получить профиль здоровья по user id.

#### GET `/api/users/{id}/health/{healthId}`

Назначение: получить профиль по profile id с проверкой принадлежности.

#### DELETE `/api/users/{id}/health/{healthId}`

Назначение: удалить профиль здоровья.

### 7.4. Medical View History API

#### GET `/api/users/{id}/views`

Назначение: получить список просмотренных препаратов.

Response: `List<MedicalDto>`.

Проблема: нет проверки, что пользователь получает свою историю.

#### DELETE `/api/users/{id}/views`

Назначение: очистить историю просмотров пользователя.

Проблема: нет проверки владельца истории.

### 7.5. Email Verification API

#### POST `/api/users/{id}/verify`

Назначение: создать запрос на подтверждение email.

Response:

```json
{
  "status": "CREATED"
}
```

Логика:

- если пользователь уже подтверждён — возвращается `VERIFIED`;
- если активная заявка есть — `409 Conflict`;
- если заявка истекла — обновляется токен и срок;
- иначе создаётся новая заявка `CREATED`.

#### GET `/api/users/{id}/verify?token={uuid}`

Назначение: подтвердить email.

Response:

```json
{
  "status": "VERIFIED"
}
```
#### GET `/api/users/{id}/verify/status`

Назначение: получить статус верификации.

Response: `EmailVerificationDto`.

### 7.6. Medical Catalog API

Base URL: `/api/medicals`

#### GET `/api/medicals`

Назначение: список препаратов с фильтрами.

Query params:

| Параметр | Логика |
|---|---|
| `countryEn` | точное сравнение без учёта регистра |
| `category` | точное сравнение с `type` без учёта регистра |
| `name` | `LIKE %name%` без учёта регистра |

Пример:

```http
GET /api/medicals?countryEn=Russia&category=Жаропонижающие&name=пара
```

Response: `List<MedicalDto>`.

Кэшируется в Redis.

#### GET `/api/medicals/{id}`

Назначение: карточка препарата.

Логика:

- найти препарат;
- попытаться сохранить просмотр в историю;
- вернуть `MedicalDto`.

#### GET `/api/medicals/categories`

Назначение: список категорий. Берётся `DISTINCT type`, кэшируется в Redis.

#### GET `/api/medicals/countries?translateCountry=false`

Назначение: список стран.

- `false` — возвращает `countryRu`;
- `true` — возвращает `countryEn`.

#### GET `/api/medicals/names`

Назначение: список названий препаратов. Берётся `DISTINCT name`, кэшируется в Redis.

### 7.7. AI API

Base URL: `/api/ai`

#### GET `/api/ai/recommendation`

Назначение: получить AI-рекомендацию.

Query params:

| Параметр | Назначение |
|---|---|
| `countryEn` | Страна на английском |
| `symptoms` | В коде фактически используется как категория препарата |

Response: `String`.

Условия доступа:

- пользователь авторизован;
- `emailVerified=true`;
- `userConsent=true`;
- `privacyConsent=true`;
- профиль здоровья существует.

Особенность: `symptoms` передаётся в `medicalService.getMedicals(country, symptom, null)` вторым аргументом, то есть фактически как `category`, а не как свободное описание симптома.

#### POST `/api/ai/answer`

Назначение: задать вопрос AI-ассистенту.

Request body:

```json
{
  "text": "Можно ли принимать ибупрофен при гастрите?"
}
```

Response: `String`.

Проблема: `AnswerDto.text` не валидируется. `null` приведёт к `NullPointerException` в `AiServiceImpl.ask()`.

### 7.8. SEO API

#### GET `/api/seo/content`

Назначение: получить SEO-строку с ключевыми словами.

Response: `String`.

Логика: статичные SEO-слова + все названия препаратов из каталога.

---

## 8. Авторизация и безопасность

### Регистрация

`POST /api/users` создаёт пользователя с ролью `USER`. Пароль хэшируется через `BCryptPasswordEncoder`.

### Вход

`POST /api/login` возвращает JWT. `JwtUtil` кладёт в claims:

- `id`;
- `email`;
- `role`.

Subject токена — email пользователя.

### Проверка JWT

`JwtAuthenticationFilter`:

1. Читает `Authorization` header.
2. Проверяет префикс `Bearer `.
3. Извлекает username из JWT.
4. Загружает пользователя через `CustomUserDetailsService`.
5. Проверяет токен.
6. Заполняет `SecurityContext`.

### Публичные endpoints

В `SecurityConfig` разрешены:

```text
/api/login
POST /api/users
/swagger-ui.html
/swagger-ui/**
/api-docs/**
/api/users/*/verify
```

Остальное требует авторизации.

### Роли

Роли есть: `ADMIN`, `USER`.

### CORS

Разрешён origin:

```text
http://localhost:3000
```

Методы:

```text
GET, POST, PUT, DELETE, OPTIONS
```

Headers: `*`. Credentials: `true`.

### CSRF

CSRF отключён, сессии stateless.

---

## 9. Работа с пользователями

Реализовано:

- регистрация;
- авторизация;
- JWT;
- получение пользователя;
- список пользователей для ADMIN;
- обновление пользователя;
- удаление пользователя;
- смена пароля;
- профиль здоровья;
- email-верификация;
- роли `USER`/`ADMIN`.

---

## 10. Каталог препаратов

Препараты хранятся в `ma_medicals`. Данные загружаются SQL-файлом `db/0.0.1/medicals-fill-script-example.sql`.

Фильтрация реализована через `MedicalSpecification.byFilters`:

| Фильтр | Поле | Сравнение |
|---|---|---|
| `countryEn` | `countryEn` | exact lower-case |
| `category` | `type` | exact lower-case |
| `name` | `name` | contains lower-case |

Фактически в seed-данных найдены категории:

- `Жаропонижающие`;
- `От насморка`;
- `От боли в горле`;
- `От диареи`;
- `От запора`;
- `Противоаллергические`;
- `Противовирусные`;
- `Противовоспалительные`;
- `Противокашлевые`;
- `Спазмолитики`;
- `Успокоительные`;
- `От давления`;
- `От боли в груди`;
- `От укачивания`;
- `Высотная болезнь`;
- `Обработка ран`.

Отдельные таблицы стран, категорий и аналогов отсутствуют.

Кэш Redis:

| Данные | Ключ |
|---|---|
| Список по фильтру | `medicals:filter:countryEn=...:category=...:name=...` |
| Категории | `medicals:categories` |
| Названия | `medicals:names` |
| Страны | `medicals:countries:ru/en` |

---

## 11. Профиль здоровья пользователя

Профиль хранит:

- вес;
- хронические заболевания;
- особенности здоровья;
- аллергии.

Списки хранятся в PostgreSQL `jsonb`.

Ограничения:

- один профиль на пользователя;
- профиль обязателен для AI-модуля;
- `weight` в БД `NOT NULL`, но в DTO нет `@NotNull`;
- длина списков ограничена `@Size(max=50)`, но длина элементов списка не ограничена.

---

## 12. История просмотров и фильтров

История просмотров реализована:

- entity `MedicalViewHistory`;
- table `ma_medical_views_history`;
- endpoints `GET /api/users/{id}/views`, `DELETE /api/users/{id}/views`;
- автосохранение при `GET /api/medicals/{id}`.

Scheduler `ViewsHistoryScheduler` удаляет старые записи:

```yaml
service.delay.clearViewsHistoryDelay: 3600000
service.expired.viewsHistoryExpiredAt: 30
```

---

## 13. AI-модуль

AI-модуль реализован классами:

- `AiController`;
- `AiServiceImpl`;
- `AiClientImpl`;
- `GeminiAiConfig`;
- `GeminiAiWebClientConfig`.

Prompt-файлы:

- `prompts/recommendations-prompt.txt`;
- `prompts/answers-prompt.txt`.

Доступ к AI разрешается только при условиях:

- email подтверждён;
- пользовательское соглашение принято;
- политика конфиденциальности принята;
- профиль здоровья существует.

Gemini request отправляется через `RestClient` на URL:

```text
spring.ai.gemini.url = {base-url}/{model}:{action}
```

Default model: `gemini-flash-latest`.

AI prompt учитывает:

- страну;
- симптом/категорию;
- пол;
- дату рождения;
- вес;
- хронические заболевания;
- особенности здоровья;
- аллергии;
- до 10 препаратов из каталога.

Ограничения и риски:

---

## 14. Email-верификация

Заявка создаётся через:

```http
POST /api/users/{id}/verify
```

Токен генерируется через `UUID.randomUUID()`.

Письмо отправляет `EmailVerifiedScheduler`:

1. Удаляет истёкшие и `VERIFIED` заявки.
2. Берёт `CREATED` заявки.
3. Переводит `CREATED -> PROCESSING`.
4. Отправляет HTML-письмо.
5. Переводит заявку в `SENT`.
6. При ошибке ставит `FAILED`.

Шаблон письма: `email-verification-format-page.html`.

Подтверждение:

```http
GET /api/users/{id}/verify?token={uuid}
```

---

## 15. Работа с базой данных

БД: PostgreSQL.

Liquibase master changelog:

```text
src/main/resources/db/db.changelog-master.xml
```

Подключаются:

```text
0.0.1/changelog.xml
0.1.0/changelog.xml
0.1.1/changelog.xml
```

Таблицы:

| Таблица | Назначение |
|---|---|
| `ma_medicals` | Каталог препаратов |
| `ma_roles` | Роли |
| `ma_users` | Пользователи |
| `ma_user_health_profiles` | Профили здоровья |
| `ma_medical_views_history` | История просмотров |
| `ma_email_verifications` | Email-верификация |

ERD:

```text
ma_roles 1 ─── N ma_users
ma_users 1 ─── 0..1 ma_user_health_profiles
ma_users 1 ─── N ma_medical_views_history
ma_medicals 1 ─── N ma_medical_views_history
ma_users 1 ─── N ma_email_verifications
```

Индексы для `ma_medicals`:

- `idx_ma_medicals_type`;
- `idx_ma_medicals_country_en`;
- `idx_ma_medicals_country_ru`.

Проблемы БД:

---

## 16. Обработка ошибок

Классы исключений:

| Класс | Статус |
|---|---|
| `BadRequestException` | 400 |
| `ConflictException` | 409 |
| `NotFoundException` | 404 |
| `UserAuthorizationException` | 401 |
| `InternalServiceException` | 500 |

Глобальный обработчик: `GlobalAdviceController`.

Обрабатывает:

- `ConstraintViolationException`;
- `BaseServiceException`;
- `Exception`.

Формат ошибки:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Сообщение ошибки",
  "timestamp": "2026-04-26T10:00:00"
}
```

---

## 17. Валидация данных

Валидация находится в DTO и вызывается в основном на сервисном слое через `@Validated` + `@Valid`.

Основные ограничения:

| DTO | Ограничения |
|---|---|
| `LoginRequestDto` | `email @NotBlank @Email`, `password @NotBlank` |
| `UserRequestDto` | имя/фамилия 2–12, email только `gmail.com`, дата рождения и пол обязательны |
| `UserCreateRequestDto` | пароль 5–15 символов |
| `ResetPasswordRequestDto` | старый пароль обязательный, новый пароль 5–15 символов |
| `UserHealthProfileRequestDto` | `weight > 0`, списки максимум 50 элементов |

---

## 18. Swagger / OpenAPI

Swagger подключён через Springdoc OpenAPI.

Доступ:

```text
http://localhost:8090/swagger-ui.html
http://localhost:8090/api-docs
```

Настроен bearer JWT:

```
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
```

## 19. Примеры сценариев использования

### Сценарий 1. Регистрация пользователя

1. Frontend вызывает `POST /api/users`.
2. Backend валидирует DTO.
3. Backend проверяет уникальность email.
4. Backend хэширует пароль.
5. Backend назначает роль `USER`.
6. Backend сохраняет пользователя.
7. Backend возвращает `UserDto`.

### Сценарий 2. Авторизация

1. Frontend вызывает `POST /api/login`.
2. Backend проверяет email/password.
3. Backend генерирует JWT.
4. Frontend сохраняет token и отправляет его в `Authorization: Bearer ...`.

### Сценарий 3. Поиск препарата

1. Frontend вызывает `GET /api/medicals?countryEn=Russia&category=Жаропонижающие&name=пара`.
2. Backend проверяет Redis.
3. При cache miss выполняет JPA Specification query.
4. Backend сохраняет результат в Redis.
5. Backend возвращает список `MedicalDto`.

### Сценарий 4. Просмотр карточки препарата

1. Frontend вызывает `GET /api/medicals/{id}`.
2. Backend получает препарат.
3. Backend пытается сохранить историю просмотра.
4. Backend возвращает карточку препарата.

### Сценарий 5. Создание профиля здоровья

1. Frontend вызывает `POST /api/users/{id}/health`.
2. Backend проверяет пользователя.
3. Backend проверяет отсутствие существующего профиля.
4. Backend сохраняет профиль.
5. Backend возвращает `UserHealthProfileDto`.

### Сценарий 6. Email-верификация

1. Пользователь вызывает `POST /api/users/{id}/verify`.
2. Backend создаёт заявку `CREATED`.
3. Scheduler отправляет письмо и переводит заявку в `SENT`.
4. Пользователь переходит по ссылке `GET /api/users/{id}/verify?token=...`.
5. Backend подтверждает email.

В текущей реализации шаг 4 требует JWT и нуждается в доработке.

### Сценарий 7. AI-рекомендация

1. Пользователь должен быть авторизован, подтверждён, иметь согласия и профиль здоровья.
2. Frontend вызывает `GET /api/ai/recommendation?countryEn=Russia&symptoms=Жаропонижающие`.
3. Backend получает профиль здоровья.
4. Backend подбирает до 10 препаратов из каталога.
5. Backend формирует prompt.
6. Backend отправляет запрос в Gemini.
7. Backend возвращает текст AI-ответа.

---

## 20. Итоговое резюме

Backend Medical Applied — это Spring Boot монолит для медицинского справочного приложения. Он реализует пользователей, JWT-авторизацию, каталог препаратов, поиск, профиль здоровья, историю просмотров, email-верификацию, AI-рекомендации, Redis-кэш, Liquibase-миграции и Swagger.

