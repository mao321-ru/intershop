# intershop: "Витрина интернет-магазина"

Демонстрационное веб-приложение с использованием Spring Boot / Spring WebFlux / Spring Data R2DBC / PostgreSQL / Redis.

## Установка приложения в Docker

В случае доступности Docker Compose можно установить и запустить приложение командой:

```cmd
  docker compose up --build --detach
```
После установки приложение будет доступно по URL:

[http://localhost:8085](http://localhost:8085)

После установки приложения нужно добавить товары на витрину:
[/config](http://localhost:8085/config)

Команда для остановки и удаления приложения:

```cmd
  docker compose down
```

## Запуск тестов

Предварительные требования:
- Java 21 (например, Eclipse Temurin OpenJDK 21.0.5+11)
- Docker (для создания и подключения к тестовой БД PostgreSQL)

Тесты запускаются командой:

```cmd
  ./gradlew cleanTest test
```

## Сборка приложения

Предварительные требования:
- Java 21 (например, Eclipse Temurin OpenJDK 21.0.5+11)

Для сборки используется Gradle, команда:

```cmd
   ./gradlew clean bootJar
```

## Установка приложения

Предварительные требования:
- PostgreSQL 17.x (например PostgreSQL 17.2)
- Redis (например, Redis 7.4.2)

Порядок установки:

- создать пользователя в PostgreSQL

Пример создания пользователя javadev с паролем javadev скриптом src/main/resources/db/init/10_javadev.sql с помощью утилиты командой строки psql:

```cmd
  psql postgresql://postgres@localhost:5432/postgres -f src/main/resources/db/init/10_javadev.sql
```

- создать БД в PostgreSQL

Пример создания БД intershopdb, принадлежащей пользователю javadev скриптом src/main/db/init/20_intershopdb.sql:

```cmd
  psql postgresql://javadev@localhost:5432/postgres -f src/main/resources/db/init/20_intershopdb.sql
```

- запустить Redis

При доступности Docker можно запустить в контейнере командой:

```cmd
  docker run -d -p 6379:6379 redis:7.4.2-alpine3.21
```

- выполнить [сборку приложения](#Сборка-приложения)

- запустить приложение в консоли командой (прервать выполнение можно по Ctrl-C)

```cmd
  java -jar build/libs/intershop-0.0.3-SNAPSHOT.jar
```

После запуска приложение будет доступно по URL:

[http://localhost:8085](http://localhost:8085)
