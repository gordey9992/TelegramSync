# 🔮 TelegramSync - Синхронизация Minecraft и Telegram

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green.svg)
![Java](https://img.shields.io/badge/Java-21-red.svg)
![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)

**Мощная синхронизация чата между Minecraft сервером и Telegram** - общайтесь с игроками из любого места!

> ✨ **Авторы:** [gordey25690](https://github.com/gordey25690) и [DeepSeek](https://github.com/DeepSeek-AI)

## 🎯 Возможности

### 🔄 Двусторонняя синхронизация
- **Minecraft → Telegram**: Все сообщения чата, события игроков, системные уведомления
- **Telegram → Minecraft**: Сообщения из Telegram отображаются в игровом чате
- **Поддержка плагинов**: XFilesPlugin, EssentialsXChat и другие

### 🤖 Умная интеграция
- **Фильтрация сообщений**: Блокировка спама и рекламы
- **Форматирование**: Красивые сообщения с эмодзи и цветами
- **Авто-уведомления**: Вход/выход игроков, достижения, смерти
- **Безопасность**: Проверка прав доступа и ограничение длины сообщений

### 🛠 Управление
- **Команды администрирования**: Статус, трансляции, перезагрузка
- **Гибкая конфигурация**: Полная настройка через YAML файлы
- **Локализация**: Полная поддержка русского языка
- **Логирование**: Детальные логи всех операций

## 🚀 Быстрый старт

### Установка
1. **Скачайте последнюю версию** из [Releases](https://github.com/gordey25690/TelegramSync/releases)
2. **Поместите JAR-файл** в папку `plugins/` вашего сервера
3. **Перезагрузите сервер** или выполните `plugman reload TelegramSync`

### Настройка бота
1. **Создайте бота** через [@BotFather](https://t.me/BotFather) в Telegram
2. **Получите Chat ID** через [@userinfobot](https://t.me/userinfobot)
3. **Настройте config.yml**:
```yaml
telegram:
  bot-token: "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"
  chat-id: "-1001234567890"
  enabled: true
