package au.com.beba.runninggoal

import au.com.beba.runninggoal.domain.core.Distance
import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceTest {

    @Test
    fun testFromMetres() {
        getInputOutputTuples().forEach {
            assertEquals("%s not matching expected %s".format(it.first, it.second), it.second, Distance.fromMetres(it.first).value)
        }
    }

    private fun getInputOutputTuples(): List<Pair<Long, Float>> {
        return listOf(
                Pair(0L, 0f),
                Pair(1L, 0f),
                Pair(10L, 0f),
                Pair(100L, 0.1f),
                Pair(101L, 0.1f),
                Pair(149L, 0.1f),
                Pair(150L, 0.2f),
                Pair(151L, 0.2f),
                Pair(199L, 0.2f),
                Pair(200L, 0.2f),
                Pair(1000L, 1.0f),
                Pair(1000L, 1.0f),
                Pair(1000L, 1.0f),
                Pair(1999L, 2.0f),
                Pair(10000L, 10f),
                // REAL EXAMPLES
                Pair(60649L, 60.6f),
                Pair(59687L, 59.7f),
                Pair(49140L, 49.1f),
                Pair(20923L, 20.9f)
        )
    }
}