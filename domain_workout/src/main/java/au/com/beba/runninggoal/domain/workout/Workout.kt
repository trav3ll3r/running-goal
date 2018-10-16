package au.com.beba.runninggoal.domain.workout

/**
 * Workout data model
 */
data class Workout(val name: String?,
                   val description: String?,
                   val distance: Float,
                   val dateTime: Long, val id: Long = 0L) {

    val distanceInMetres: Long
        get() {
            return distance.toLong()
        }
}
