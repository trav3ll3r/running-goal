package au.com.beba.runninggoal.models

import au.com.beba.runninggoal.domain.GoalDate
import au.com.beba.runninggoal.domain.Period
import org.junit.Assert.assertEquals
import org.junit.Test

class PeriodTotalDaysTest {

    @Test
    fun getForOneDay() {
        val p = Period(GoalDate(2018, 8, 1), GoalDate(2018, 8, 1))
        assertEquals(1, p.totalDays)
    }

    @Test
    fun getForAug2018() {
        val p = Period(GoalDate(2018, 8, 1), GoalDate(2018, 8, 31))
        assertEquals(31, p.totalDays)
    }

    @Test
    fun getForSep2018() {
        val p = Period(GoalDate(2018, 9, 1), GoalDate(2018, 9, 30))
        assertEquals(30, p.totalDays)
    }
}