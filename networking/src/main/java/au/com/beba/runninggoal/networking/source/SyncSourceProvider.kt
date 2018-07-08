package au.com.beba.runninggoal.networking.source

import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import java.time.LocalDate


interface SyncSourceProvider {
    fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile)
    suspend fun getDistanceForDateRange(start: LocalDate, end: LocalDate): Float
}