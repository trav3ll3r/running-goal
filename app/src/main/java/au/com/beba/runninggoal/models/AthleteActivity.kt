package au.com.beba.runninggoal.models

/**
 * Athlete Activity data model
 */
data class AthleteActivity(val id: Long, val name: String, val distance: Float, val private: Boolean) {

    constructor() : this(0, "", 0f, false) {
        // EMPTY CONSTRUCTOR
    }
}
