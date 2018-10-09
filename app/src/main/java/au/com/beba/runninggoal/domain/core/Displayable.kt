package au.com.beba.runninggoal.domain.core

import android.text.Spannable

interface Displayable {
    fun display(smallerDecimals: Boolean = false): Spannable
    fun displaySigned(smallerDecimals: Boolean = false): Spannable
}