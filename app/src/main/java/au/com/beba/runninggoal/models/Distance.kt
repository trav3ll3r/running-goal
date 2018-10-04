package au.com.beba.runninggoal.models

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE
import android.text.style.RelativeSizeSpan
import android.util.Log
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class Distance : Displayable {

    val value: Float
    val units: String

    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)

    companion object {
        fun fromMetres(metres: Long): Distance {
            val roundedDistance: Float = "%.1f".format(metres / 1000f).toFloat()
            return Distance(roundedDistance)
        }
    }

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
            Log.d("Distance", "segments | value=%s".format(value))

            val valueTokens = value.toString().split(".")
            val iPart = valueTokens[0].toInt()
            val fPart = valueTokens[1].toInt()

            Log.d("Distance", "segments | iPart=%s fPart=%s".format(iPart, fPart))
            return Pair(iPart, fPart)
        }

    init {
        numberFormat.minimumFractionDigits = 1
        numberFormat.maximumFractionDigits = 1
        numberFormat.roundingMode = RoundingMode.HALF_UP
    }

    override fun display(): String {
        return numberFormat.format(value)
    }

    override fun displayReduced(): Spannable {
        val value = numberFormat.format(value)
        val result = SpannableString(value)
        if (value.contains('.')) {
            val startChar = value.indexOf(".")
            val endChar = value.length
            result.setSpan(RelativeSizeSpan(0.8f), startChar, endChar, SPAN_EXCLUSIVE_INCLUSIVE) // reduce font size by 20%
        }
        return result
    }

    override fun displaySigned(): String {
        val sign = if (value > 0) "+" else ""
        return "%s%s".format(sign, numberFormat.format(value))
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

interface Displayable {
    fun display(): String
    fun displayReduced(): Spannable
    fun displaySigned(): String
}