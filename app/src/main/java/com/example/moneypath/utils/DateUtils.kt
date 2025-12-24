package com.example.moneypath.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

// Функція яка перетворює звичайний час в unix
fun dateToUnix(date:String):Long{
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = sdf.parse(date) ?: Date()
    return parsedDate.time/1000
}

// Зворотня функція
fun unixToDate(unixTime: Long) :String{
    val date = Date(unixTime*1000)
    val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    return sdf.format(date)
}

fun formattedDate(date: Long):String{
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val selectedDate = Calendar.getInstance().apply { timeInMillis = date * 1000 }

    return when {
        selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Сьогодні"

        selectedDate.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> "Вчора"

        else -> unixToDate(date)
    }
}

// Початок і кінець дня за датою
fun getDayRangeFromUnix(unixTimestamp: Long): Pair<Long, Long> {
    val secondsInDay = 86400L
    val dateStart = (unixTimestamp / secondsInDay) * secondsInDay         // початок дня
    val dateEnd = dateStart + secondsInDay - 1                             // кінець дня
    return dateStart to dateEnd
}

fun getTodayDate(): Long{
    return Calendar.getInstance().timeInMillis
}

// Функція для обрахунку кінця поточного (дата кінця поточного місяця)
// та стабільного плану (дата поочаток наступного місяця + months)
fun calculatePlanDates(months: Int?): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    // 1. Кінець поточного місяця
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    val currentPlanEnd = calendar.timeInMillis

    if(months != null) {
        // 2. Початок наступного місяця
        val nextMonthCalendar = Calendar.getInstance()
        nextMonthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        nextMonthCalendar.set(Calendar.HOUR_OF_DAY, 0)
        nextMonthCalendar.set(Calendar.MINUTE, 0)
        nextMonthCalendar.set(Calendar.SECOND, 0)

        //  3. Кінець stable плану
        val stablePlanCalendar = nextMonthCalendar.clone() as Calendar
        stablePlanCalendar.add(Calendar.MONTH, months)
        stablePlanCalendar.set(
            Calendar.DAY_OF_MONTH,
            stablePlanCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        stablePlanCalendar.set(Calendar.HOUR_OF_DAY, 23)
        stablePlanCalendar.set(Calendar.MINUTE, 59)
        stablePlanCalendar.set(Calendar.SECOND, 59)
        val stablePlanEnd = stablePlanCalendar.timeInMillis
        return Pair(currentPlanEnd, stablePlanEnd)
    } else return Pair(currentPlanEnd, 0L)
}

data class TimeDiff(val months: Int, val days: Int)

// Рахуємо кількість місяців і днів між датами
fun calculateMonthsAndDaysBetween(startMillis: Long, endMillis: Long): TimeDiff {
    val start = Calendar.getInstance().apply { timeInMillis = startMillis }
    val end = Calendar.getInstance().apply { timeInMillis = endMillis }

    // Визначаємо порядок (щоб не мати від’ємних значень)
    val isNegative = end.before(start)
    if (isNegative) {
        val temp = start.clone() as Calendar
        start.timeInMillis = endMillis
        end.timeInMillis = temp.timeInMillis
    }

    var years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR)
    var months = end.get(Calendar.MONTH) - start.get(Calendar.MONTH)
    var days = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)

    if (days < 0) {
        // Позичаємо один місяць, якщо днів не вистачає
        months -= 1
        val temp = Calendar.getInstance()
        temp.timeInMillis = end.timeInMillis
        temp.add(Calendar.MONTH, -1)
        days += temp.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    if (months < 0) {
        months += 12
        years -= 1
    }

    // Усе зводимо до загальної кількості місяців + днів
    val totalMonths = abs(years * 12 + months)
    val totalDays = abs(days)

    return TimeDiff(months = totalMonths, days = totalDays)
}

// Функція для визначення дати початку та кінця місяця
fun getMonthBounds(date: Long = 0L): Pair<Long, Long> {
    val calendar = if(date == 0L) {
        Calendar.getInstance()
    }else{
        Calendar.getInstance().apply {
            timeInMillis = date
        }
    }
    // Початок місяця
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfMonth = calendar.timeInMillis/1000

    // Кінець місяця
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfMonth = calendar.timeInMillis/1000

    return Pair(startOfMonth, endOfMonth)
}

// Функція форматування місяць рік
fun formatMonthYear(startSec: Long, endSec: Long): String {
    val startCal = Calendar.getInstance().apply { timeInMillis = startSec * 1000 }
    val endCal = Calendar.getInstance().apply { timeInMillis = endSec * 1000 }

    val currentMonth = Calendar.getInstance()
    val prevMonth = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }

    return when {
        startCal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                startCal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) -> "Поточний місяць"

        startCal.get(Calendar.MONTH) == prevMonth.get(Calendar.MONTH) &&
                startCal.get(Calendar.YEAR) == prevMonth.get(Calendar.YEAR) -> "Попередній місяць"

        else -> {
            val monthName =
                startCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val year = startCal.get(Calendar.YEAR)
            "$monthName $year"
        }
    }
}

