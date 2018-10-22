package au.com.beba.runninggoal.ui.component

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE
import android.text.style.RelativeSizeSpan
import au.com.beba.runninggoal.domain.Distance
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


fun Distance.display(smallerDecimals: Boolean = false): Spannable {
    return displayReduced(numberFormat.format(value), smallerDecimals)
}

fun Distance.displaySigned(smallerDecimals: Boolean = false): Spannable {
    val sign = if (value > 0) "+" else ""
    return displayReduced("%s%s".format(sign, numberFormat.format(value)), smallerDecimals)
}

//TODO: MOVE TO some "UI Render" class
private fun displayReduced(text: String, smallerDecimals: Boolean = false): Spannable {
    val result = SpannableString(text)
    val startChar = text.indexOf(".")
    if (smallerDecimals && startChar > 0) {
        val endChar = text.length
        result.setSpan(RelativeSizeSpan(0.8f), startChar, endChar, SPAN_EXCLUSIVE_INCLUSIVE) // reduce font size by 20%
    }
    return result
}

private val floatFormatter: NumberFormat
    get() {
        val numberFormat = NumberFormat.getInstance(Locale.ENGLISH)
        numberFormat.minimumFractionDigits = 0
        numberFormat.maximumFractionDigits = 1
        numberFormat.roundingMode = RoundingMode.HALF_UP
        return numberFormat
    }

fun Float.display(smallerDecimals: Boolean = false): Spannable {
    return displayReduced(floatFormatter.format(this), smallerDecimals)
}

fun Float.displaySigned(smallerDecimals: Boolean = false): Spannable {
    val sign = if (this > 0) "+" else ""
    return displayReduced("%s%s".format(sign, floatFormatter.format(this)), smallerDecimals)
}