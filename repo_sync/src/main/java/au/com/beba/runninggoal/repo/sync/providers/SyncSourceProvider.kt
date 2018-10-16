package au.com.beba.runninggoal.repo.sync.providers

import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.domain.workout.sync.ApiSourceProfile


interface SyncSourceProvider {

    fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile)

    suspend fun getWorkoutsForDateRange(startTime: Long, endTime: Long): List<Workout>
}