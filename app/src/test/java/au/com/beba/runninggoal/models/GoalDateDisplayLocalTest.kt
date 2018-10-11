package au.com.beba.runninggoal.models

import au.com.beba.runninggoal.domain.GoalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime


class GoalDateDisplayLocalTest {

    /**
     * Test 01 Sep 2018 00:00:00.0 +08:00 (Australia/Sydney)
     */
    @Test
    fun displayLocalFor01Sep2018MinSydney() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 1, 0, 0, 0, 0, ZoneOffset.of("+10:00"))
        val obj = GoalDate(zoned)

        assertEquals("01/09/2018", obj.asDisplayLocalLong())
    }

    /**
     * Test 01 Sep 2018 00:00:00.0 +08:00 (Australia/Perth)
     */
    @Test
    fun displayLocalFor01Sep2018MinPerth() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 1, 0, 0, 0, 0, ZoneOffset.of("+08:00"))
        val obj = GoalDate(zoned)

        assertEquals("01/09/2018", obj.asDisplayLocalLong())
    }

    /**
     * Test 30 Sep 2018 23:59:59.0 +10:00 (Australia/Sydney)
     */
    @Test
    fun displayLocalFor30Sep2018MaxSydney() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 30, 23, 59, 59, 0, ZoneOffset.of("+10:00"))
        val obj = GoalDate(zoned)

        assertEquals("30/09/2018", obj.asDisplayLocalLong())
    }

    /**
     * Test 30 Sep 2018 23:59:59.0 +01:00 (England/London)
     */
    @Test
    fun displayLocalFor01Sep2018MinLondon() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 1, 0, 0, 0, 0, ZoneOffset.of("+01:00"))
        val obj = GoalDate(zoned)

        assertEquals("01/09/2018", obj.asDisplayLocalLong())
    }

}