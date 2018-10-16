package au.com.beba.runninggoal.domain.core

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE
import android.text.style.RelativeSizeSpan
import au.com.beba.runninggoal.domain.Distance


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
