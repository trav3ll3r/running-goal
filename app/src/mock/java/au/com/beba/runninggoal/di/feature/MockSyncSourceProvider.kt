package au.com.beba.runninggoal.di.feature

import au.com.beba.runninggoal.networking.source.CommonSyncSourceProvider
import java.time.LocalDate

class MockSyncSourceProvider : CommonSyncSourceProvider() {
    override suspend fun getDistanceForDateRange(start: LocalDate, end: LocalDate): Float {
        Thread.sleep(2000)
        return 12345.67f
    }
}