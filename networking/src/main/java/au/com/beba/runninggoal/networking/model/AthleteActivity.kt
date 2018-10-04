package au.com.beba.runninggoal.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Simple model to map JSON payload into AthleteActivity model
 */
@Deprecated(message = "Domain models reside in Domain", replaceWith = ReplaceWith("domain.model.Workout"))
data class AthleteActivity(
        val dateTime: Long,
        val name: String,
        val description: String,
        @SerializedName("distance")
        val distanceInMetres: Float
)
