package au.com.beba.runninggoal.models

import java.time.LocalDate

data class RunningGoal(
        val id: Int,
        var name: String,
        var target: GoalTarget,
        var progress: GoalProgress = GoalProgress(),
        var projection: GoalProjection = GoalProjection(),
        var view: GoalView = GoalView()
)

data class GoalTarget(val distance: Int, val start: LocalDate, val end: LocalDate)

data class GoalProgress(
        var distanceToday: Double = 0.0,
        var daysTotal: Int = 0,
        var daysLapsed: Int = 0,
        var distanceExpected: Double = 0.0,
        var positionInDistance: Double = 0.0,
        var positionInDays: Double = 0.0)

data class GoalProjection(val distancePerDay: Double = 0.0, val daysLapsed: Int = 0)

data class GoalView(var viewType: GoalViewType = GoalViewType.PROGRESS_BAR) {
    fun toggle() {
        viewType = if (viewType == GoalViewType.PROGRESS_BAR) {
            GoalViewType.NUMBERS
        } else {
            GoalViewType.PROGRESS_BAR
        }
    }
}

enum class GoalViewType(private val dbValue: Int) {
    PROGRESS_BAR(0),
    NUMBERS(1);

    companion object {
        fun fromDbValue(value: Int): GoalViewType {
            return GoalViewType.values().find { it.dbValue == value }!!
        }
    }

    fun asDbValue(): Int {
        return dbValue
    }

}