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

Начальный баланс во встроенном сервисе платежей (модуль paysrv) берется из конфига и составляет 1000 (сбрасывается при рестарте сервиса).

Команда для остановки и удаления приложения:

```cmd
  docker compose down
```

## Запуск тестов

Предварительные требования:
- Java 21 (например, Eclipse Temurin OpenJDK 21.0.5+11)
- Docker (для создания и подключения к тестовой БД PostgreSQL)

Порядок выполнения:

- запустить в консоли платежный сервис для использования в тестах

```cmd
  ./gradlew paysrv:bootRun --args="--spring.profiles.active=apptest"
```

- запустить в другой консоли тесты командой

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

Пример создания пользователя javadev с паролем javadev скриптом app/src/main/resources/db/init/10_javadev.sql с помощью утилиты командой строки psql:

```cmd
  psql postgresql://postgres@localhost:5432/postgres -f app/src/main/resources/db/init/10_javadev.sql
```

- создать БД в PostgreSQL

Пример создания БД intershopdb, принадлежащей пользователю javadev скриптом app/src/main/db/init/20_intershopdb.sql:

```cmd
  psql postgresql://javadev@localhost:5432/postgres -f app/src/main/resources/db/init/20_intershopdb.sql
```

- запустить Redis

При доступности Docker можно запустить в контейнере командой:

```cmd
  docker run -d -p 6379:6379 redis:7.4.2-alpine3.21
```
- запустить локальный авторизационный сервер Keycloak

При доступности Docker можно запустить в контейнере командой:

```cmd
  docker run -d -p 8087:8080 --name keycloak \
      -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
      -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
      quay.io/keycloak/keycloak:26.1.3 start-dev
```

После запуска авторизационный сервер будет доступно по URL (пользователь admin, пароль admin):
[http://localhost:8087](http://localhost:8087)

Нужно залогиниться и с помощью Clients -> Import client импортировать настройки клиента intershop из файла:

./app/intershop.json

- выполнить [сборку приложения](#Сборка-приложения)

- запустить платежный сервис в консоли командой (прервать выполнение можно по Ctrl-C)

```cmd
  java -jar paysrv/build/libs/paysrv-0.0.1-SNAPSHOT.jar
```

- запустить приложение в консоли командой (прервать выполнение можно по Ctrl-C)

```cmd
  java -jar app/build/libs/intershop-0.0.3-SNAPSHOT.jar
```

После запуска приложение будет доступно по URL:

[http://localhost:8085](http://localhost:8085)
