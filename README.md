# Основные возможности

- Запуск сервиса и базы данных в Docker-контейнерах (`docker-compose up -d`).

- Использование `.env` для конфигурации переменных среды, не требует редактирования самого `docker-compose.yml`.

- В папке postman_collection находится коллекция для Postman, содержащая готовые запросы.

- В папке jmeter_config находится конфигурационный файл для JMeter.

# Нагрузочное тестирование

Примеры конфигурации JMeter.

![Настройка jmeter 1](https://github.com/pentryyy/wallet-manager-api/blob/main/images/jmeter_settings_1.png)

![Настройка jmeter 2](https://github.com/pentryyy/wallet-manager-api/blob/main/images/jmeter_settings_2.png)

![Настройка jmeter 3](https://github.com/pentryyy/wallet-manager-api/blob/main/images/jmeter_settings_3.png)

Результаты нагрузочного тестирования показали, что веб-сервис способен выдерживать 1000+ запросов в секунду.

![Тест результат](https://github.com/pentryyy/wallet-manager-api/blob/main/images/rps_result.png)

![Тест график](https://github.com/pentryyy/wallet-manager-api/blob/main/images/rps_graph.png)