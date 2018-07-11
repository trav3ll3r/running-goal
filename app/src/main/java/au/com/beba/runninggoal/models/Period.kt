package au.com.beba.runninggoal.models

import java.time.Duration
import java.time.LocalDate

class Period(val from: LocalDate = LocalDate.now(), val to: LocalDate = LocalDate.now(), private val today: LocalDate = LocalDate.now()) {

    val daysLapsed: Int
        get() {
            return daysBetween(from, lapsedEndDate)
        }

    val totalDays: Int = daysBetween(from, to)

    // INTERNAL UTIL
    private val lapsedEndDate: LocalDate = if (to.isAfter(today)) today else to

    private fun daysBetween(from: LocalDate, to: LocalDate): Int {
        var daysBetween = 0
        // CALCULATE WHEN from==to OR from IS BEFORE to
        if (!from.isAfter(to)) {
            daysBetween = Duration.between(from.atTime(0, 0), to.atTime(23, 59)).toDays().toInt()
            daysBetween++
        }
        return daysBetween
    }
}