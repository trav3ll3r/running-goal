package au.com.beba.runninggoal.sync

import au.com.beba.runninggoal.domain.Workout


interface SyncSourceProvider {

    fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile)

    suspend fun getWorkoutsForDateRange(startTime: Long, endTime: Long): List<Workout>
}