package com.homefit.app

// The schedule uses ISO weekdays (Monday = 1); platform calendars start with Sunday.
internal fun isoWeekday(sundayFirstDay: Int): Int = (sundayFirstDay + 5) % 7 + 1

internal expect fun currentLocalWeekday(): Int
