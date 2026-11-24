package com.benzomageddon

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val moneyFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))

fun formatMoney(amount: Double): String {
    val rounded = kotlin.math.round(amount * 100) / 100
    return moneyFormat.format(rounded) + " ₽"
}

fun formatShortDate(timestamp: Long): String =
    Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))

fun dayStart(date: LocalDate): Long =
    date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun monthStart(yearMonth: YearMonth): Long =
    dayStart(yearMonth.atDay(1))

fun monthEnd(yearMonth: YearMonth): Long =
    dayStart(yearMonth.atEndOfMonth().plusDays(1)) - 1

fun currentMonth(): YearMonth =
    YearMonth.now()
