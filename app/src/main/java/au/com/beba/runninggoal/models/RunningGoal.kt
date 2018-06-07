package au.com.beba.runninggoal.models

import java.time.LocalDate

data class RunningGoal(val id: String, val name: String, var target: GoalTarget, var progress: GoalProgress? = null, var projection: GoalProjection? = null)

data class GoalTarget(val distance: Int, val start: LocalDate, val end: LocalDate)

data class GoalProgress(val distanceToday: Int, val daysLapsed: Int, val distanceExpected: Double)

data class GoalProjection(val distancePerDay: Double, val daysLapsed: Int)
