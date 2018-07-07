package au.com.beba.runninggoal

import au.com.beba.runninggoal.models.Distance
import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceTest {

    @Test
    fun testFromMetres() {
        getInputOutputTuples().forEach {
            assertEquals("%s not matching expected %s".format(it.first, it.second), it.second, Distance.fromMetres(it.first).value)
        }
    }

    private fun getInputOutputTuples(): List<Pair<Float, Float>> {
        return listOf(
                Pair(0f, 0f),
                Pair(1f, 0f),
                Pair(10f, 0f),
                Pair(100f, 0.1f),
                Pair(101f, 0.1f),
                Pair(149f, 0.1f),
                Pair(150f, 0.2f),
                Pair(151f, 0.2f),
                Pair(199f, 0.2f),
                Pair(200f, 0.2f),
                Pair(1000f, 1.0f),
                Pair(1000.1f, 1.0f),
                Pair(1000.5f, 1.0f),
                Pair(1999.9f, 2.0f),
                Pair(10000f, 10f),
                // REAL EXAMPLES
                Pair(60649.402f, 60.6f),
                Pair(59687.797f, 59.7f),
                Pair(49140.900f, 49.1f),
                Pair(20923.197f, 20.9f)
        )
    }
}