package au.com.beba.runninggoal.networking.model

/**
 * Simple model to map JSON payload into AthleteActivity model
 */
data class AthleteActivity(val name: String, val distance: Float, val private: Boolean)
