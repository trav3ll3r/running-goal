package au.com.beba.runninggoal.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.beba.runninggoal.R


class NumericWithLabel : ConstraintLayout {

    private val current: TextView
    private val units: TextView

    companion object {
        private val TAG = NumericWithLabel::class.java.simpleName
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
        var styledValue: String? = null
        var styledUnit: String? = null
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumericWithLabel,
                0, 0).apply {

            try {
                styledValue = getString(R.styleable.NumericWithLabel_NumericWithLabel_numericValue)
                styledUnit = getString(R.styleable.NumericWithLabel_NumericWithLabel_unitValue)
            } finally {
                recycle()
            }
        }

        setValues(styledValue ?: "n/a", styledUnit ?: "n/a")
    }

    fun setValues(current: String, units: String) {
        Log.d(TAG, "setValues")
        this.current.text = current
        this.units.text = units
    }
}