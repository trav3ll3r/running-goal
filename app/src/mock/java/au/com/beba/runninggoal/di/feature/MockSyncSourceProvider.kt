package au.com.beba.runninggoal.di.feature

import au.com.beba.runninggoal.networking.model.AthleteActivity
import au.com.beba.runninggoal.networking.source.CommonSyncSourceProvider

class MockSyncSourceProvider : CommonSyncSourceProvider() {
    override suspend fun getAthleteActivitiesForDateRange(startTime: Long, endTime: Long): List<AthleteActivity> {
        Thread.sleep(2000)
        return listOf(
                AthleteActivity(0, "WU", "", 1800f),
                AthleteActivity(0, "Parkrun", "", 5000f),
                AthleteActivity(0, "Commute to work", "", 5123f),
                AthleteActivity(0, "KR Classic", "", 6605f),
                AthleteActivity(0, "10k", "", 10000f),
                AthleteActivity(0, "city2surf", "", 14000f),
                AthleteActivity(0, "Half Marathon", "", 21100f),
                AthleteActivity(0, "Marathon", "", 42200f)
        )
    }
}