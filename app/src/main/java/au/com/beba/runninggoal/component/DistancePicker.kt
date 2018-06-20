package au.com.beba.runninggoal.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.NumberPicker
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.Distance


class DistancePicker : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val integerPicker: NumberPicker
    private val fractionPicker: NumberPicker

    init {
        LayoutInflater.from(context).inflate(R.layout.decimal_number_picker, this)

        integerPicker = findViewById(R.id.integer_picker)
        integerPicker.minValue = 1
        integerPicker.maxValue = 1000

        fractionPicker = findViewById(R.id.fraction_picker)
        fractionPicker.minValue = 0
        fractionPicker.maxValue = 9

    }

    fun setValue(value: Distance) {
        val segments: Pair<Int, Int> = value.segments
        integerPicker.value = segments.first
        fractionPicker.value = segments.second
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        return false
    }

    override fun performLongClick(): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}