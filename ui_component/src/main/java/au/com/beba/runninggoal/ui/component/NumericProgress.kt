package au.com.beba.runninggoal.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class NumericProgress : ConstraintLayout {

    private val current: TextView
    private val total: TextView
    private val units: TextView

    companion object {
        private val TAG = NumericProgress::class.java.simpleName
        private const val EMPTY_VALUE: String = "-"
    }
    private val logger: Logger = LoggerFactory.getLogger(TAG)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        stylise(attrs)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.component_numeric_progress, this)

        current = findViewById(R.id.numeric_progress_current)
        total = findViewById(R.id.numeric_progress_total)
        units = findViewById(R.id.numeric_progress_units)
    }

    private fun stylise(attrs: AttributeSet?) {
        var styledCurrent: String? = null
        var styledTotal: String? = null
        var styledUnit: String? = null
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumericProgress,
                0, 0).apply {

            try {
                styledCurrent = getString(R.styleable.NumericProgress_currentValue)
                styledTotal = getString(R.styleable.NumericProgress_totalValue)
                styledUnit = getString(R.styleable.NumericProgress_unitValue)
            } finally {
                recycle()
            }
        }

        setValues(styledCurrent, styledTotal, styledUnit)
    }

    fun setValues(current: CharSequence?, total: CharSequence?, units: CharSequence?) {
        logger.debug("setValues")
        this.current.text = current ?: EMPTY_VALUE
        this.total.text = total ?: EMPTY_VALUE
        this.units.text = units ?: EMPTY_VALUE
    }
}