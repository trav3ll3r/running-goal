package au.com.beba.runninggoal.domain.workout.sync

data class StravaSourceType(override val iconRes: Int, override val nameRes: Int) : SourceType {
    override val type: String = "STRAVA"
}