# Benzomageddon

Трекер расходов для Android (Kotlin, Jetpack Compose, MVVM).

## Возможности (MVP)

- Учёт доходов и расходов: сумма, категория, дата, комментарий, тип платы (наличные/карта)
- Категории: 12 предустановленных (8 расходных, 4 доходных)
- Главный экран: баланс, траты за сегодня и за месяц, последние операции, курсы USD/EUR
- Статистика: итоги месяца, круговая диаграмма расходов по категориям, разбивка доходов
- Календарь: просмотр операций по дням, переход между месяцами
- Контроль лимита: месячный лимит расходов, прогресс-бар, предупреждение при 80% и превышении
- Настройки: установка/удаление лимита, очистка данных

## Стек

- Kotlin + Jetpack Compose (Material 3)
- Архитектура MVVM + Clean Architecture (View → ViewModel → Repository → Room/DataStore)
- Room (локальная БД), DataStore (настройки), Retrofit (курсы валют, open.er-api.com)
- Hilt (DI), Navigation Compose
- R8/ProGuard для release-сборки

## Сборка

Требуется JDK 17 и Android SDK (platform 35, build-tools 35).

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Структура

```
app/src/main/java/com/benzomageddon/
├── data/           # Room entities, DAO, database, repositories, network
├── di/             # Hilt modules
├── viewmodel/      # ViewModels (Home, Stats, Calendar, Settings, TransactionForm)
├── ui/             # Compose screens, navigation, components, theme
└── Format.kt       # форматирование сумм и дат
```

## Скриншоты

<!-- screenshots -->

## Автор

Смирнов Александр — студент 4 курса, БГТУ «ВОЕНМЕХ» (ИСиТ)
