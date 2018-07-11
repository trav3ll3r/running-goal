package au.com.beba.runninggoal.models

import java.time.LocalDate


data class RunningGoal(
        val id: Int = 0,
        var name: String = "",
        var target: GoalTarget = GoalTarget(),
        var progress: GoalProgress = GoalProgress(),
        var projection: GoalProjection = GoalProjection(),
        var view: GoalView = GoalView()
) {
    fun updateProgressValues(onDate: LocalDate = LocalDate.now()) {
        target.period = Period(target.period.from, target.period.to, onDate)
        setProgress(this, onDate)
    }

    private fun setProgress(runningGoal: RunningGoal, today: LocalDate) {
        val currentDistance = runningGoal.progress.distanceToday.value

        val daysTotal = runningGoal.target.period.totalDays
        val daysLapsed = runningGoal.target.period.daysLapsed

        val linearDistancePerDay = (runningGoal.target.distance.value / daysTotal)
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(Distance(currentDistance), daysTotal, daysLapsed, Distance(expectedDistance))
        runningGoal.progress.positionInDistance = Distance(currentDistance - expectedDistance)
        runningGoal.progress.positionInDays = runningGoal.progress.positionInDistance.value / linearDistancePerDay
        runningGoal.progress.status = runningGoal.getStatus(today)

        runningGoal.projection = GoalProjection(Distance(linearDistancePerDay), daysLapsed)
    }

    private fun getStatus(onDate: LocalDate): GoalStatus {
        return when {
            onDate.isBefore(target.period.from) -> GoalStatus.NOT_STARTED
            onDate.isAfter(target.period.to) -> GoalStatus.EXPIRED
            else -> GoalStatus.ONGOING
        }
    }
}

data class GoalTarget(
        var distance: Distance = Distance(1f),
        var period: Period = Period()
)

data class GoalProgress(
        var distanceToday: Distance = Distance(),
        var daysTotal: Int = 0,
        var daysLapsed: Int = 0,
        var distanceExpected: Distance = Distance(),
        var positionInDistance: Distance = Distance(),
        var positionInDays: Float = 0.0f,
        var status: GoalStatus = GoalStatus.UNKNOWN)

data class GoalProjection(val distancePerDay: Distance = Distance(), val daysLapsed: Int = 0)

data class GoalView(
        var viewType: GoalViewType = GoalViewType.PROGRESS_BAR,
        var updating: Boolean = false) {
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

enum class GoalStatus {
    UNKNOWN,
    NOT_STARTED,
    ONGOING,
    EXPIRED
}