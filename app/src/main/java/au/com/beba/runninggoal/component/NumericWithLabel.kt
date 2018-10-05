package au.com.beba.runninggoal.component

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.beba.runninggoal.R
import org.jetbrains.anko.textColorResource


class NumericWithLabel : ConstraintLayout {

    private val current: TextView
    private val units: TextView

    companion object {
        private val TAG = NumericWithLabel::class.java.simpleName

        private val STYLE_ATTRS = intArrayOf(android.R.attr.textSize, android.R.attr.textColor)
        @ColorRes
        private val DEFAULT_VALUE_COLOR: Int = R.color.primary_text
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        stylise(attrs)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.component_numeric_with_label, this)

        current = findViewById(R.id.numeric_value)
        units = findViewById(R.id.numeric_label)
    }

    private fun stylise(attrs: AttributeSet?) {
        var styledNumericValue: String? = null
        @ColorInt var styledNumericColor: Int = R.color.primary_text
        var styledUnit: String? = null

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumericWithLabel,
                0, 0).apply {

            try {
                styledNumericValue = getString(R.styleable.NumericWithLabel_NumericWithLabel_numericValue)
                styledNumericColor = getResourceId(R.styleable.NumericWithLabel_NumericWithLabel_numericColor, DEFAULT_VALUE_COLOR)
                styledUnit = getString(R.styleable.NumericWithLabel_NumericWithLabel_unitValue)
            } finally {
                recycle()
            }
        }

//        // OBTAIN android:style REFERENCE
//        @StyleRes val styleId = attrs?.styleAttribute ?: 0
//        // APPLY @style VALUES
//        if (styleId > 0) {
//        }

        setValues(styledNumericValue ?: "n/a", styledUnit ?: "n/a")
        this.current.textColorResource = styledNumericColor
        this.units.textColorResource = styledNumericColor
    }

    fun setValues(current: String, units: String) {
        setValues(SpannableString(current), units)
    }

    fun setValues(current: CharSequence, units: CharSequence) {
        Log.d(TAG, "setValues")
        this.current.text = current
        this.units.text = units
    }
}