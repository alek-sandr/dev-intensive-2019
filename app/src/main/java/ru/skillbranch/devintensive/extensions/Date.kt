package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy") : String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(other: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = other.time / DAY
    return day1 == day2
}

fun Date.add(value: Int, timeUnit: TimeUnits = TimeUnits.SECOND) : Date {
    this.time += when(timeUnit) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val difference = date.time - time
    return formatDiff(difference / SECOND)
}

private fun formatDiff(seconds: Long) : String {
    val result = StringBuilder()

    when(abs(seconds)) {
        in 0..1 -> result.append("только что")
        in 1..45 -> result.append("несколько секунд назад")
        in 45..75 -> result.append("минуту назад")
        in 75..(45 * 60) -> {
            if (seconds < 0) {
                result.append("через ")
            }
            val minutes = abs(seconds / 60).toInt()
            result.append(TimeUnits.MINUTE.plural(minutes))
            if (seconds > 0) {
                result.append(" назад")
            }
        }
        in (45 * 60)..(75 * 60) -> {
            if (seconds < 0) {
                result.append("через час")
            } else {
                result.append("час назад")
            }
        }
        in (75 * 60)..(22 * 3600) -> {
            if (seconds < 0) {
                result.append("через ")
            }
            val hours = abs(seconds / 3600).toInt()
            result.append(TimeUnits.HOUR.plural(hours))
            if (seconds > 0) {
                result.append(" назад")
            }
        }
        in (22 * 3600)..(26 * 3600) -> {
            if (seconds < 0) {
                result.append("через день")
            } else {
                result.append("день назад")
            }
        }
        in (26 * 3600)..(360 * 24 * 3600) -> {
            if (seconds < 0) {
                result.append("через ")
            }
            val days = abs(seconds / (24 * 3600)).toInt()
            result.append(TimeUnits.DAY.plural(days))
            if (seconds > 0) {
                result.append(" назад")
            }
        }
        else -> {
            if (seconds < 0) {
                result.append("более чем через год")
            } else {
                result.append("более года назад")
            }
        }
    }
    return result.toString()
}

enum class TimeUnits {
    SECOND, MINUTE, HOUR, DAY;

    fun plural(value: Int): String {
        val last = value % 10
        return "$value" + when(this) {
            SECOND -> when(last) {
                1 -> " секунду"
                2, 3, 4 -> " секунды"
                0, 5, 6, 7, 8, 9 -> " секунд"
                else -> " Oops"
            }
            MINUTE -> when(last) {
                1 -> " минуту"
                2, 3, 4 -> " минуты"
                0, 5, 6, 7, 8, 9 -> " минут"
                else -> " Oops"
            }
            HOUR -> when(last) {
                1 -> " час"
                2, 3, 4 -> " часа"
                0, 5, 6, 7, 8, 9 -> " часов"
                else -> " Oops"
            }
            DAY -> when(last) {
                1 -> " день"
                2, 3, 4 -> " дня"
                0, 5, 6, 7, 8, 9 -> " дней"
                else -> " Oops"
            }
        }
    }
}