package au.com.beba.runninggoal.networking.source

import java.time.LocalDate

interface ApiSource {
    suspend fun getDistanceForDateRange(start: LocalDate, end: LocalDate): Float
}