package au.com.beba.runninggoal.models

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class Distance : Displayable {

    val value: Float
    val units: String

    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)

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
            val iPart = value.toInt()
            val fPart = ((value - iPart.toFloat()) * 10).toInt()
            return Pair(iPart, fPart)
        }

    init {
        numberFormat.minimumFractionDigits = 0
        numberFormat.maximumFractionDigits = 1
        numberFormat.roundingMode = RoundingMode.HALF_UP
    }

    override fun display(): String {
        return "%s".format(numberFormat.format(value))
    }

    override fun displaySigned(): String {
        val sign = if (value > 0) "+" else ""
        return "%s%s".format(sign, numberFormat.format(value))
    }
}

interface Displayable {
    fun display(): String
    fun displaySigned(): String
}