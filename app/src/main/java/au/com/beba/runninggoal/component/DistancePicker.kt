package au.com.beba.runninggoal.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.NumberPicker
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.Distance


class DistancePicker : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private val TAG = DistancePicker::class.java.simpleName
    }

    private val integerPicker: NumberPicker
    private val fractionPicker: NumberPicker

    init {
        LayoutInflater.from(context).inflate(R.layout.decimal_number_picker, this)

        integerPicker = findViewById(R.id.integer_picker)
        integerPicker.minValue = 0
        integerPicker.maxValue = 100000

        fractionPicker = findViewById(R.id.fraction_picker)
        fractionPicker.minValue = 0
        fractionPicker.maxValue = 9
    }

    fun getValue(): Distance {
        return Distance("%s.%s".format(integerPicker.value, fractionPicker.value))
    }

    fun setValue(value: Distance) {
        Log.d(TAG, "setValue | value=%s".format(value.value))
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