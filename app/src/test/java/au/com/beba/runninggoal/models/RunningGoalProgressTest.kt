package au.com.beba.runninggoal.models

//class RunningGoalProgressTest {
//
//    @Test
//    fun updateProgressValues() {
//        val startDate = LocalDate.of(2018, Month.JULY, 2)
//
//        val r = RunningGoal(0, "Weekly 70", GoalTarget(
//                Distance(70.0f),
//                Period(LocalDate.of(2018, Month.JULY, 2), LocalDate.of(2018, Month.JULY, 8))
//        ))
//
//        r.updateProgressValues(startDate.plusDays(-1))
//        assertEquals(0, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(0.0f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(0))
//        assertEquals(1, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(10f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(1))
//        assertEquals(2, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(20f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(2))
//        assertEquals(3, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(30f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(3))
//        assertEquals(4, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(40f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(4))
//        assertEquals(5, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(50f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(5))
//        assertEquals(6, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(60f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(6))
//        assertEquals(7, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(70f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(7))
//        assertEquals(7, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(70f), r.progress.distanceExpected)
//
//        r.updateProgressValues(startDate.plusDays(8))
//        assertEquals(7, r.progress.daysLapsed)
//        assertEquals(7, r.progress.daysTotal)
//        assertEquals(Distance(70f), r.progress.distanceExpected)
//    }
//
//    @Test
//    fun oneWeekPeriodTest() {
//        val from: LocalDate = LocalDate.of(2018, Month.JULY, 2)
//        val to: LocalDate = LocalDate.of(2018, Month.JULY, 8)
//
//        val r = RunningGoal(0, "Weekly 70", GoalTarget(
//                Distance(70.0f),
//                Period(from, to)
//        ))
//
//        // TEST BEFORE PERIOD'S FIRST DAY
//        r.updateProgressValues(from.plusDays(-1))
//        assertEquals("Using -1 day not matching expected value", 0, r.target.period.daysLapsed)
//        assertEquals(7, r.target.period.totalDays)
//        assertEquals(Distance(0f), r.progress.distanceExpected)
//
//        // TEST PERIOD'S FIRST DAY
//        r.updateProgressValues(from)
//        assertEquals("Using first day not matching expected value", 1, r.target.period.daysLapsed)
//        assertEquals(7, r.target.period.totalDays)
//        assertEquals(Distance(10f), r.progress.distanceExpected)
//
//        // TEST [1, 6] RANGE
//        (1L .. 6L).forEach {
//            r.updateProgressValues(from.plusDays(it))
//            assertEquals("Adding $it day(s) not matching expected value", (it+1).toInt(), r.target.period.daysLapsed)
//            assertEquals(7, r.target.period.totalDays)
//            assertEquals(Distance(10f * (it+1f)), r.progress.distanceExpected)
//        }
//
//        // TEST PERIOD'S LAST DAY
//        r.updateProgressValues(to)
//        assertEquals("Using last day not matching expected value", 7, r.target.period.daysLapsed)
//        assertEquals(7, r.target.period.totalDays)
//        assertEquals(Distance(70f), r.progress.distanceExpected)
//
//        // TEST PAST PERIOD'S LAST DAY
//        r.updateProgressValues(from.plusDays(20))
//        assertEquals("Using 20 days not matching expected value", 7, r.target.period.daysLapsed)
//        assertEquals(7, r.target.period.totalDays)
//        assertEquals(Distance(70f), r.progress.distanceExpected)
//    }
//}