package com.homefit.app

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitWeekday
import platform.Foundation.NSDate

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
internal actual fun currentLocalWeekday(): Int =
    isoWeekday(NSCalendar.currentCalendar.component(NSCalendarUnitWeekday, NSDate()).toInt())
