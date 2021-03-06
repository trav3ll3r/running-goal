package au.com.beba.runninggoal.domain


data class RunningGoal(
        val id: Long = 0,
        var name: String = "",
        var target: GoalTarget = GoalTarget(),
        var progress: GoalProgress = GoalProgress(),
        var projection: GoalProjection = GoalProjection(),
        var view: GoalView = GoalView(),
        var deleted: Boolean = false
) {
    fun updateProgressValues(onDate: GoalDate = GoalDate()) {
        target.period = Period(target.period.from, target.period.to, onDate)
        setProgress(this, onDate)
    }

    private fun setProgress(runningGoal: RunningGoal, today: GoalDate) {
        val currentDistance = runningGoal.progress.distanceToday.value

        val daysTotal = runningGoal.target.period.totalDays
        val daysLapsed = runningGoal.target.period.daysLapsed

        val linearDistancePerDay = (runningGoal.target.distance.value / daysTotal)
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.let {

            progress = GoalProgress(Distance(currentDistance), daysTotal, daysLapsed, Distance(expectedDistance))
            progress.positionInDistance = Distance(currentDistance - expectedDistance)
            progress.positionInDays = progress.positionInDistance.value / linearDistancePerDay
            progress.status = getStatus(today)

            val daysRemaining = daysTotal - daysLapsed
            val distanceRemaining = runningGoal.target.distance.value - currentDistance
            val remainingDistancePerDay = when {
                daysRemaining > 0 -> distanceRemaining / daysRemaining
                else -> -1f
            }
            projection = GoalProjection(Distance(linearDistancePerDay), Distance(remainingDistancePerDay))
        }
    }

    private fun getStatus(onDate: GoalDate): GoalStatus {
        return when {
            onDate.isBefore(target.period.from) -> GoalStatus.NOT_STARTED
            onDate.isAfter(target.period.to) -> GoalStatus.EXPIRED
            else -> GoalStatus.ONGOING
        }
    }

    fun expired(): Boolean {
        return progress.status == GoalStatus.EXPIRED
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

data class GoalProjection(val distancePerDayPeriod: Distance = Distance(), val distancePerDayRemaining: Distance = Distance())

data class GoalView(var updating: Boolean = false)

enum class GoalStatus {
    UNKNOWN,
    NOT_STARTED,
    ONGOING,
    EXPIRED;
}