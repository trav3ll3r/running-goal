package au.com.beba.runninggoal.networking.source

import au.com.beba.runninggoal.networking.model.ApiSourceProfile


interface SyncSourceProvider {
    fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile)
    suspend fun getDistanceForDateRange(startTime: Long, endTime: Long): Float
}