# DailyNotepad-Phoenix
"DailyNotepad-Phoenix : A complete rewrite of the original DailyNotepad project. Rebuilt from scratch with improved architecture, better code organization, and additional features for daily note-taking.

## Kafka & Redis Integration

### Kafka
- Используется для публикации событий о заметках (создание, изменение, удаление).
- Топик по умолчанию `note-events`.
- Для локального запуска поднимите Kafka (например, через Docker) и создайте топик:
  ```bash
  docker run -d --name kafka -p 9092:9092 apache/kafka:latest
  kafka-topics.bat --create --topic note-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
  ```

### Redis
- Используется как кэш для чтения заметок по ID и по дате.
- По умолчанию ожидает Redis на `localhost:6379`.
- Запуск через Docker:
  ```bash
  docker run -d --name redis -p 6379:6379 redis:7-alpine
  ```
- Настраиваемые параметры: `spring.redis.host`, `spring.redis.port`, `app.cache.ttl.minutes`.
- Кэшируемые методы:
  - `NoteService.findNotesByDate(LocalDate date)` → кэш `notesByDate`
  - `NoteService.findNoteById(Long id)` → кэш `notesById`
- После операций записи кэши инвалидируются через `NoteCacheService`.