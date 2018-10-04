package au.com.beba.runninggoal.networking.source

import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.model.AthleteActivity


interface SyncSourceProvider {
    fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile)
    suspend fun getAthleteActivitiesForDateRange(startTime: Long, endTime: Long): List<AthleteActivity>
}