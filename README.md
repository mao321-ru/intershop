# intershop: "Витрина интернет-магазина"

Демонстрационное веб-приложение с использованием Spring Boot / Spring Data JPA / Hibernate ORM.

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

