package au.com.beba.runninggoal.domain.core

/**
 * Athlete Activity data model
 * TODO: RENAME INTO Workout
 */
data class Workout(val name: String?, val description: String?, val distanceInMetres: Long, val dateTime: Long, val id: Long = 0L) {

//    constructor() : this("", "", 0f, GoalDate().asEpochUtc()) {
//        // EMPTY CONSTRUCTOR
//    }
}
