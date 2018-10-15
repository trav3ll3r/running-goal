package au.com.beba.runninggoal.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class Distance {

    companion object {
        private val TAG = Distance::class.java.simpleName

        fun fromMetres(metres: Long): Distance {
            val roundedDistance: Float = "%.1f".format(metres / 1000f).toFloat()
            return Distance(roundedDistance)
        }
    }

    val value: Float
    val units: String

    val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    constructor(stringValue: String, units: String = "km") {
        // PARSE
        this.value = numberFormat.parse(stringValue).toFloat()
        this.units = units
    }

    @JvmOverloads
    constructor(value: Float = 0.0f, units: String = "km") {
        // PARSE
        this.value = value
        this.units = units
    }

    val segments: Pair<Int, Int>
        get() {
            logger.debug("segments | value=%s".format(value))

            val valueTokens = value.toString().split(".")
            val iPart = valueTokens[0].toInt()
            val fPart = valueTokens[1].toInt()

            logger.debug("segments | iPart=%s fPart=%s".format(iPart, fPart))
            return Pair(iPart, fPart)
        }

    init {
        numberFormat.minimumFractionDigits = 1
        numberFormat.maximumFractionDigits = 1
        numberFormat.roundingMode = RoundingMode.HALF_UP
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Distance

        if (value != other.value) return false
        if (units != other.units) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + units.hashCode()
        return result
    }
}