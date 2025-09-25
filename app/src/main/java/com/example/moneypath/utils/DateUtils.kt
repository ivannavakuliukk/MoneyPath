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


