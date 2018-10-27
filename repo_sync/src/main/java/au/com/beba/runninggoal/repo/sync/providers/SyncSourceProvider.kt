package au.com.beba.runninggoal.repo.sync.providers

import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.domain.workout.sync.SyncSource


interface SyncSourceProvider {

    fun setSyncSourceProfile(syncSource: SyncSource)

    suspend fun getWorkoutsForDateRange(startTime: Long, endTime: Long): List<Workout>
}