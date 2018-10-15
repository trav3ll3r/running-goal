package au.com.beba.runninggoal.component

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.stylist.getStyleColour
import au.com.beba.runninggoal.stylist.getStyleDimensionPixelSize
import au.com.beba.runninggoal.stylist.getStyleInteger
import org.jetbrains.anko.textColor
import timber.log.Timber


class NumericWithLabel : ConstraintLayout {

    private val current: TextView
    private val units: TextView

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
        var numericTextValue: String? = null
        var labelTextValue: String? = null

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumericWithLabel,
                0, 0).apply {

            try {
                numericTextValue = getString(R.styleable.NumericWithLabel_NumericWithLabel_numericText)
                labelTextValue = getString(R.styleable.NumericWithLabel_NumericWithLabel_labelText)
            } finally {
                recycle()
            }
        }

        val DEFAULT_STYLE = R.style.GoalValueLabel

        // RESOLVE theme STYLE (USING ?attr/myCustomStyleName INSIDE A THEME)
        //@StyleRes val themeStyle = context.getThemeStyle(styleId) ?: R.style.GoalValueLabel

        applyStyleValues(attrs?.styleAttribute ?: DEFAULT_STYLE)

        setValues(numericTextValue ?: "n/a", labelTextValue ?: "n/a")
    }

    private fun applyStyleValues(styleId: Int) {
        initNumericStyle(styleId)
        initLabelStyle(styleId)
    }

    private fun initNumericStyle(@StyleRes styleId: Int) {
        val textColor = context.getStyleColour(styleId, R.attr.numericTextColor)
        val textSize = context.getStyleDimensionPixelSize(styleId, R.attr.numericTextSize)

        current.textColor = textColor
        current.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    private fun initLabelStyle(@StyleRes styleId: Int) {
        val textColor = context.getStyleColour(styleId, R.attr.labelTextColor)
        val textSize = context.getStyleDimensionPixelSize(styleId, R.attr.labelTextSize)
        val textVisibility = context.getStyleInteger(styleId, R.attr.labelVisibility)

        units.textColor = textColor
        units.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        units.visibility = textVisibility
    }

    fun setValues(current: String, units: String) {
        setValues(SpannableString(current), units)
    }

    fun setValues(current: CharSequence, units: CharSequence) {
        Timber.d("setValues")
        this.current.text = current
        this.units.text = units
    }
}