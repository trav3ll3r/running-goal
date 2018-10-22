package au.com.beba.runninggoal.feature.sync

//TODO: REFACTOR TO WORK INSIDE ANDROID LIB
class MockSyncSourceProvider /*: CommonSyncSourceProvider()*/ {

    companion object {
        private val TAG = MockSyncSourceProvider::class.java.simpleName
    }

//    override suspend fun getWorkoutsForDateRange(startTime: Long, endTime: Long): List<Workout> {
//        Log.i(TAG, "getWorkoutsForDateRange")
//        Thread.sleep(2000)
//        Log.i(TAG, "getWorkoutsForDateRange | return list")
//        return listOf(
//                Workout("WU", "", 1800, 0),
//                Workout("Parkrun", "", 5000, 0),
//                Workout("Commute to work", "", 5123, 0),
//                Workout("KR Classic", "", 6605, 0),
//                Workout("10k", "", 10000, 0),
//                Workout("city2surf", "", 14000, 0),
//                Workout("Half Marathon", "", 21100, 0),
//                Workout("Marathon", "", 42200, 0)
//        )
//    }
}