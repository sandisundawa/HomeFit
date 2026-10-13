package com.homefit.app

import java.util.Calendar

internal actual fun currentLocalWeekday(): Int = isoWeekday(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
