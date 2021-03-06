package au.com.beba.runninggoal.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.*
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import au.com.beba.runninggoal.domain.GoalDate
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.widget.Widget
import au.com.beba.runninggoal.domain.widget.WidgetViewType
import au.com.beba.runninggoal.ui.component.display
import au.com.beba.runninggoal.ui.component.displaySigned
import org.intellij.lang.annotations.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class GoalWidgetRenderer {

    companion object {
        private val TAG = GoalWidgetRenderer::class.java.simpleName
        const val FLIP_CLICKED = "au.com.beba.runninggoal.FLIP_CLICKED"
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    fun updateUi(context: Context, rootView: RemoteViews, runningGoal: RunningGoal?, widget: Widget) {
        logger.info("updateUi")

        if (runningGoal?.deleted != false || runningGoal.id < 1) {
            GoalDeletedRenderer().render(context, rootView)
            return
        }

        rootView.setTextViewText(R.id.goal_name, "%s".format(runningGoal.name))
        rootView.setTextViewText(R.id.goal_period, "%s to %s (%s days)".format(
                DateRenderer.asFullDate(runningGoal.target.period.from),
                DateRenderer.asFullDate(runningGoal.target.period.to),
                runningGoal.progress.daysTotal
        ))

        when (widget.view.viewType) {
            WidgetViewType.PROGRESS_BAR -> {
                logger.debug("updateUi | PROGRESS_BAR | runningGoal=%s".format(runningGoal.id))
                rootView.setViewVisibility(R.id.goal_in_visuals, View.VISIBLE)
                rootView.setViewVisibility(R.id.goal_in_numbers, View.GONE)
                rootView.setImageViewIcon(R.id.btn_flip, Icon.createWithResource(context, R.drawable.ic_list_24dp))

                GoalAsProgressRenderer().render(context, rootView, runningGoal)
            }
            WidgetViewType.NUMBERS -> {
                logger.debug("updateUi | NUMBERS | runningGoal=%s".format(runningGoal.id))
                rootView.setViewVisibility(R.id.goal_in_visuals, View.GONE)
                rootView.setViewVisibility(R.id.goal_in_numbers, View.VISIBLE)
                rootView.setImageViewIcon(R.id.btn_flip, Icon.createWithResource(context, R.drawable.ic_pie_chart_24dp))

                GoalInNumbersRenderer().render(context, rootView, runningGoal)
            }
        }

        // TODO: TAPPING ON WIDGET OPENS EditGoalActivity
        // ENTIRE Widget CATCHES CLICK AND FIRES "edit" INTENT
        // Create the "edit" Intent to launch Activity
        //val intent = EditGoalActivity.buildIntent(context, runningGoal.id)
        //val pendingIntentEdit = PendingIntent.getActivity(context, runningGoal.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        //rootView.setOnClickPendingIntent(R.id.widget_root, pendingIntentEdit)

        // Create an Intent to change Goal's GoalViewType
        rootView.setOnClickPendingIntent(R.id.btn_flip, getPendingSelfIntent(context, FLIP_CLICKED, widget.id))
    }

    private fun getPendingSelfIntent(context: Context, action: String, widgetId: Int): PendingIntent {
        val intent = Intent(context, RunningGoalWidgetProvider::class.java)
        intent.action = action
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

class GoalInNumbersRenderer {
    companion object {
        private val TAG = GoalInNumbersRenderer::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    fun render(context: Context, rootView: RemoteViews, runningGoal: RunningGoal) {
        logger.info("render")
        val progress = runningGoal.progress
        val projections = runningGoal.projection

        //FIXME: AVOID DEPENDENCY ON App's DisplayExt.kt
        // ROW 1
        addNumericView(context, rootView, R.id.hex_center, "Km", progress.distanceToday.display(), runningGoal.target.distance.display())
        addNumericView(context, rootView, R.id.hex_top_left, "Days", progress.daysLapsed.toString(), progress.daysTotal.toString())
        addNumericView(context, rootView, R.id.hex_bottom_left, "Average", projections.distancePerDayPeriod.displaySigned(), units = "km/day")

        // ROW 2
        addNumericView(context, rootView, R.id.hex_top_right, "Position", progress.positionInDistance.displaySigned(), units = "km")
        addNumericView(context, rootView, R.id.hex_bottom_right, "Position", DecimalRenderer.fromFloat(progress.positionInDays, true), units = "day(s)")
    }

    private fun addNumericView(context: Context, rootView: RemoteViews, @Identifier holderViewId: Int, name: String, value: CharSequence, valueMax: CharSequence? = null, units: String? = null) {
        val view = RemoteViews(context.packageName, R.layout.goal_partial)
        view.setTextViewText(R.id.goal_part_name, name)
        view.setTextViewText(R.id.goal_part_value, value)
        view.setViewVisibility(R.id.goal_part_out_of, View.GONE)
        if (valueMax != null) {
            view.setViewVisibility(R.id.goal_part_out_of, View.VISIBLE)
            view.setTextViewText(R.id.goal_part_out_of, "/%s".format(valueMax))
        }
        view.setViewVisibility(R.id.goal_part_unit, View.GONE)
        if (units != null) {
            view.setViewVisibility(R.id.goal_part_unit, View.VISIBLE)
            view.setTextViewText(R.id.goal_part_unit, units)
        }

        rootView.removeAllViews(holderViewId)
        rootView.addView(holderViewId, view)
    }
}

class GoalAsProgressRenderer {
    companion object {
        private val TAG = GoalAsProgressRenderer::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val gapAngle = 30f
    private val startAngle = 90f + gapAngle
    private val fullSweep = 360f - (gapAngle * 2)

    private var widthMax: Int = 0
    private var heightMax: Int = 0
    private lateinit var notchesSpecs: Notches

    private data class Notches(
            val days: Int,
            val notches: Int,
            val fullAngle: Float = 360.0f,
            val notchWidth: Float = 0.30f
    ) {
        val notchOffsetAngle: Float
            get() {
                return fullAngle / days
            }
    }

    fun render(context: Context, rootView: RemoteViews, runningGoal: RunningGoal) {
        logger.info("render")
        logger.debug("render | runningGoal=%s".format(runningGoal.id))
        widthMax = context.resources.getDimensionPixelSize(R.dimen.max_width)
        heightMax = context.resources.getDimensionPixelSize(R.dimen.max_height)

        //Paint for arc stroke.
        val paint = Paint(FILTER_BITMAP_FLAG or DITHER_FLAG or ANTI_ALIAS_FLAG)
        paint.strokeCap = Paint.Cap.ROUND

        //Paint for text values.
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = (context.resources.getDimensionPixelSize(R.dimen.widget_text_large_value)).toFloat()
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER

        val bitmap = Bitmap.createBitmap(widthMax, heightMax, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val max = runningGoal.target.distance.value
        val expected = runningGoal.progress.distanceExpected.value
        val current = runningGoal.progress.distanceToday.value
        val positionInDistance = runningGoal.progress.positionInDistance.value

        notchesSpecs = Notches(
                days = runningGoal.progress.daysTotal,
                notches = runningGoal.progress.daysTotal - 1,
                fullAngle = fullSweep
        )

        circleBase(context, canvas, paint)
        arcBase(context, canvas)
        arcCurrent(context, canvas, current / max)
        arcProgressNotches(context, canvas)

        //Draw text value
        val centreWidth = (bitmap.width / 2).toFloat()
        val centreHeight = (bitmap.height - textPaint.ascent()) / 2
//        val alignBottom = (bitmap.height - (stroke + 0))
        //canvas.drawText( "%s%%".format(percentage), centreWidth, centreHeight, textPaint)
        //Draw widget title.
        //textPaint.setTextSize((context.getResources().getDimension(R.dimen.widget_text_large_title) / density) as Int)
        // CURRENT DISTANCE
        canvas.drawText(
                "%s".format(current),
                centreWidth, centreHeight, textPaint)

        // POSITION (DISTANCE)
        textPaint.textSize = (context.resources.getDimensionPixelSize(R.dimen.widget_text_small_value)).toFloat()
        canvas.drawText(
                "(%s)".format(DecimalRenderer.fromFloat(positionInDistance, true)),
                centreWidth, centreHeight - textPaint.ascent() * 3f, textPaint)

        rootView.setImageViewBitmap(R.id.progress_view, bitmap)

        val factor = expected / max
        val expectedAngle = gapAngle + (factor * fullSweep)
        renderExpectedMarker(context, rootView, expectedAngle)
    }

    private fun renderExpectedMarker(context: Context, rootView: RemoteViews, angle: Float) {
        val markerIcon = Icon.createWithResource(context, R.drawable.ic_navigation_24dp)
        val markerDrawable = markerIcon.loadDrawable(context)
        markerDrawable.setTint(ContextCompat.getColor(context, R.color.marker_color))

        if (markerDrawable != null) {
            val bitmapIcon = placeMarkerAndRotation(context, markerDrawable, angle)
            rootView.setImageViewBitmap(R.id.progress_expected_flag_img, bitmapIcon)
        }
    }

    private fun circleBase(context: Context, canvas: Canvas, paint: Paint) {
        //First draw full arc as background.
        paint.color = Color.argb(75, 0, 0, 0)
        val stroke = context.resources.getDimensionPixelSize(R.dimen.base_ring_stroke).toFloat()
        paint.strokeWidth = stroke
        paint.style = Paint.Style.FILL
        val arcRect = getArcRect(stroke, 0)
        canvas.drawCircle(arcRect.centerX(), arcRect.centerY(), widthMax / 2f, paint)
    }

    private fun arcBase(context: Context, canvas: Canvas) {
        drawArc(context, canvas, Color.argb(55, 255, 255, 255), startAngle, fullSweep)
    }

    private fun arcCurrent(context: Context, canvas: Canvas, percentage: Float) {
        val visiblePercentage = if (percentage > 1.0f) 1f else percentage
        drawArc(context, canvas, Color.argb(150, 0, 255, 0), arcLengthAngle = fullSweep * visiblePercentage)
    }

    private fun arcProgressNotches(context: Context, canvas: Canvas) {
        val fmt = DecimalFormat.getInstance()
        fmt.minimumFractionDigits = 1
        fmt.maximumFractionDigits = 1
        val colour = Color.argb(200, 0, 0, 0)

        var correctedArcStartAngle = startAngle
        (0..notchesSpecs.days).forEach {
            val arcStartAngle = when (it) {
                0 -> correctedArcStartAngle
                else -> correctedArcStartAngle + notchesSpecs.notchOffsetAngle
            }
            correctedArcStartAngle = fmt.format(arcStartAngle).toFloat()
            if (correctedArcStartAngle > 360.00f) {
                correctedArcStartAngle -= 360.00f
            }
            logger.debug("arcProgressNotches | notch=%s correctedArcStartAngle=%s sweep=%s".format(it, correctedArcStartAngle, notchesSpecs.notchOffsetAngle))

            val notchScale = if ((it + 0) % 5 == 0) 0.5f else 0.2f // every 5th notch is longer
            drawArc(context, canvas, colour, correctedArcStartAngle, notchesSpecs.notchWidth, notchScale = notchScale)
        }
    }

    private fun getArcRect(stroke: Float, padding: Int, width: Int? = null, height: Int? = null): RectF {
        val arcWidth = width ?: widthMax
        val arcHeight = height ?: heightMax
        logger.debug("width=%s height=%s".format(arcWidth, arcHeight))
        val arcRect = RectF()
        arcRect.set((stroke / 2 + padding), (stroke / 2 + padding), (arcWidth - padding - stroke / 2), (arcHeight - padding - stroke / 2))
        return arcRect
    }

    private fun drawArc(context: Context, canvas: Canvas, colour: Int,
                        arcStartAngle: Float = startAngle,
                        arcLengthAngle: Float,
                        notchScale: Float = 1.0f) {
        val center = Point(canvas.width / 2, canvas.height / 2)
        val outerRadius = (canvas.width.toFloat() / 2) - context.resources.getDimensionPixelSize(R.dimen.base_ring_padding)
        val arcWidth = context.resources.getDimensionPixelSize(R.dimen.inner_ring_stroke)
        val innerRadius = outerRadius - (arcWidth * notchScale)

        val outerRect = RectF(center.x - outerRadius, center.y - outerRadius, center.x + outerRadius, center.y + outerRadius)
        val innerRect = RectF(center.x - innerRadius, center.y - innerRadius, center.x + innerRadius, center.y + innerRadius)

        val path = Path()
        path.arcTo(outerRect, arcStartAngle, arcLengthAngle)
        path.arcTo(innerRect, arcStartAngle + arcLengthAngle, -arcLengthAngle)
        path.close()

        val fill = Paint(FILTER_BITMAP_FLAG or DITHER_FLAG or ANTI_ALIAS_FLAG)
        fill.color = colour
        canvas.drawPath(path, fill)

        // STROKE THE PATH
        val border = Paint(FILTER_BITMAP_FLAG or DITHER_FLAG or ANTI_ALIAS_FLAG)
        border.style = Paint.Style.STROKE
        border.color = colour
        border.strokeWidth = 1f
        canvas.drawPath(path, border)
    }

    private fun placeMarkerAndRotation(context: Context, drawableIcon: Drawable, angle: Float): Bitmap {
        val max = widthMax
        val bitmap = Bitmap.createBitmap(max, max, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // TODO: FOR TEST ONLY
//        val paint = Paint()
//        paint.color = Color.argb(30, 0, 0, 0)
//        canvas.drawRect(0f, 0f, max.toFloat(), max.toFloat(), paint)

        if (angle > 0) {
            canvas.rotate(angle, widthMax.toFloat() / 2, widthMax.toFloat() / 2)
        }

        //SET iconHeight and iconWidth TO DESIRED SIZE (OVERRIDE NATURAL SIZE)
        val iconSize = context.resources.getDimensionPixelSize(R.dimen.marker_size)
        val iconLeft = widthMax.toFloat() / 2 - (iconSize / 2)
        val iconRight = widthMax.toFloat() / 2 + (iconSize / 2)

        val iconBottom = canvas.height
        val iconTop = iconBottom - iconSize

        drawableIcon.setBounds(iconLeft.toInt(), iconTop, iconRight.toInt(), iconBottom) // CONTROL SIZE
        drawableIcon.draw(canvas)

        return bitmap
    }
}

class GoalDeletedRenderer {
    companion object {
        private val TAG = GoalDeletedRenderer::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    fun render(context: Context, rootView: RemoteViews) {
        logger.info("render")
        rootView.setViewVisibility(R.id.goal_period, View.GONE)
        rootView.setTextViewText(R.id.goal_name, context.getString(R.string.warning_goal_for_widget_deleted))
        rootView.removeAllViews(R.id.goal_in_visuals)
        rootView.removeAllViews(R.id.goal_in_numbers)
    }
}

object DateRenderer {
    fun asFullDate(date: GoalDate): String {
        return date.asDisplayLocalShort()
    }
}

object DecimalRenderer {
    fun fromFloat(value: Float, showSigned: Boolean = false): String {
        val numberFormat = NumberFormat.getInstance(Locale.ENGLISH)
        numberFormat.minimumFractionDigits = 0
        numberFormat.maximumFractionDigits = 1
        numberFormat.roundingMode = RoundingMode.HALF_UP
        val sign = if (value > 0 && showSigned) "+" else ""
        return "%s%s".format(sign, numberFormat.format(value))
    }
}