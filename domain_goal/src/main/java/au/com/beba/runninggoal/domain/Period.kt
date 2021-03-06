package au.com.beba.runninggoal.domain

import au.com.beba.runninggoal.domain.GoalDate


class Period constructor(val from: GoalDate = GoalDate(),
                         val to: GoalDate = GoalDate(),
                         today: GoalDate = GoalDate()) {

    val daysLapsed: Int
        get() {
            return from.daysBetween(lapsedEndDate)
        }

    val totalDays: Int = from.daysBetween(to)

    // INTERNAL UTIL
    private val lapsedEndDate = if (to.isAfter(today)) today else to
}