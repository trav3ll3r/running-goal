package au.com.beba.runninggoal.models

import java.time.Duration
import java.time.LocalDate

data class RunningGoal(
        val id: Int = 0,
        var name: String = "",
        var target: GoalTarget = GoalTarget(),
        var progress: GoalProgress = GoalProgress(),
        var projection: GoalProjection = GoalProjection(),
        var view: GoalView = GoalView()
) {
    fun updateProgressValues() {
        setProgress(this, LocalDate.now())
    }

    private fun setProgress(runningGoal: RunningGoal, today: LocalDate) {
        val currentDistance = runningGoal.progress.distanceToday.value
        val endLapsedDate = getLapsedEndDate(runningGoal, today)

        val daysTotal = getTotalDaysBetween(runningGoal.target.start, runningGoal.target.end)
        //Log.d(TAG, "startDate=%s endDate=%s".format(runningGoal.target.start, endLapsedDate))
        val daysLapsed = getTotalDaysBetween(runningGoal.target.start, endLapsedDate)
        //Log.d(TAG, "daysTotal=%s daysLapsed=%s".format(daysTotal, daysLapsed))

        val linearDistancePerDay = (runningGoal.target.distance.value / daysTotal)
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(Distance(currentDistance), daysTotal, daysLapsed, Distance(expectedDistance))
        runningGoal.progress.positionInDistance = Distance(currentDistance - expectedDistance)
        runningGoal.progress.positionInDays = runningGoal.progress.positionInDistance.value / linearDistancePerDay
        runningGoal.progress.status = getStatus(runningGoal, today)

        runningGoal.projection = GoalProjection(Distance(linearDistancePerDay), daysLapsed)
    }

    private fun getLapsedEndDate(runningGoal: RunningGoal, today: LocalDate): LocalDate {
        return if (runningGoal.target.end.isAfter(today)) today else runningGoal.target.end
    }

    private fun getStatus(runningGoal: RunningGoal, today: LocalDate): GoalStatus {
        return when {
            today.isBefore(runningGoal.target.start) -> GoalStatus.NOT_STARTED
            today.isAfter(runningGoal.target.end) -> GoalStatus.EXPIRED
            else -> GoalStatus.ONGOING
        }
    }

    private fun getTotalDaysBetween(from: LocalDate, to: LocalDate): Int {
        if (from.isBefore(to)) {
            return Duration.between(from.atTime(0, 0), to.atTime(0, 0)).toDays().toInt() + 1
        }
        return 0
    }
}

data class GoalTarget(
        var distance: Distance = Distance(1f),
        var start: LocalDate = LocalDate.now(),
        val end: LocalDate = LocalDate.now()
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