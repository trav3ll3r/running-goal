package au.com.beba.runninggoal.models

/**
 * Athlete Activity data model
 * TODO: RENAME INTO Workout
 */
data class AthleteActivity(val name: String?, val description: String?, val distanceInMetres: Long, val dateTime: Long, val id: Long = 0L) {

//    constructor() : this("", "", 0f, GoalDate().asEpochUtc()) {
//        // EMPTY CONSTRUCTOR
//    }
}
