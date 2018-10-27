package au.com.beba.runninggoal.domain.workout.sync

interface SourceType {
    val type: String
    val iconRes: Int
    val nameRes: Int
}