package au.com.beba.runninggoal.models

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime

class GoalDateEpochTest {

    /**
     * Test 01 Sep 2018 00:00:00.0 +08:00 (Australia/Sydney)
     */
    @Test
    fun epochFor01Sep2018MinSydney() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 1, 0, 0, 0, 0, ZoneOffset.of("+10:00"))
        val obj = GoalDate(zoned)

        assertEquals(1535724000, obj.asEpochUtc())
        assertEquals(ZoneOffset.of("+10:00"), zoned.offset)
    }

    /**
     * Test 01 Sep 2018 00:00:00.0 +08:00 (Australia/Perth)
     */
    @Test
    fun epochFor01Sep2018MinPerth() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 1, 0, 0, 0, 0, ZoneOffset.of("+08:00"))
        val obj = GoalDate(zoned)

        assertEquals(1535731200, obj.asEpochUtc())
        assertEquals(ZoneOffset.of("+08:00"), zoned.offset)
    }

    /**
     * Test 30 Sep 2018 23:59:59.0 +10:00 (Australia/Sydney)
     */
    @Test
    fun epochFor30Sep2018MaxSydney() {
        val zoned = ZonedDateTime.of(2018, Month.SEPTEMBER.value, 30, 23, 59, 59, 0, ZoneOffset.of("+10:00"))
        val obj = GoalDate(zoned)

        assertEquals(1538315999, obj.asEpochUtc())
        assertEquals(ZoneOffset.of("+10:00"), zoned.offset)
    }
}