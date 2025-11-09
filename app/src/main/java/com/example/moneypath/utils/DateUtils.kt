package com.example.moneypath.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        nextMonthCalendar.add(Calendar.MONTH, 1)
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

