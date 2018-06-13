package au.com.beba.runninggoal.feature

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Paint.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.GoalViewType
import au.com.beba.runninggoal.models.RunningGoal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


object GoalWidgetRenderer {

    const val FLIP_CLICKED = "au.com.beba.runninggoal.FLIP_CLICKED"

    fun updateUi(context: Context, rootView: RemoteViews, runningGoal: RunningGoal) {

        rootView.setTextViewText(R.id.goal_name, "%s".format(runningGoal.name))
        rootView.setTextViewText(R.id.goal_period, "%s to %s (%s days)".format(
                DateRenderer.asFullDate(runningGoal.target.start),
                DateRenderer.asFullDate(runningGoal.target.end),
                runningGoal.progress.daysTotal
        ))

        when (runningGoal.view.viewType) {
            GoalViewType.PROGRESS_BAR -> {
                rootView.setViewVisibility(R.id.goal_in_visuals, View.VISIBLE)
                rootView.setViewVisibility(R.id.goal_in_numbers, View.GONE)
                rootView.setImageViewIcon(R.id.btn_flip, Icon.createWithResource(context, R.drawable.ic_list_24dp))

                ProgressRenderer.render(context, rootView, runningGoal)
            }
            GoalViewType.NUMBERS -> {
                rootView.setViewVisibility(R.id.goal_in_visuals, View.GONE)
                rootView.setViewVisibility(R.id.goal_in_numbers, View.VISIBLE)
                rootView.setImageViewIcon(R.id.btn_flip, Icon.createWithResource(context, R.drawable.ic_pie_chart_24dp))

                rootView.setTextViewText(R.id.goal_distance, "%s km".format(runningGoal.target.distance))

                val progress = runningGoal.progress
                rootView.setTextViewText(R.id.current_distance, "%s km".format(progress.distanceToday))
                rootView.setTextViewText(R.id.goal_days_lapsed, "%s days".format(progress.daysLapsed))
                rootView.setTextViewText(R.id.expected_distance_today, "%s km".format(DecimalRenderer.fromDouble(progress.distanceExpected)))
                rootView.setTextViewText(R.id.position_in_distance, "%s km".format(DecimalRenderer.fromDouble(progress.positionInDistance)))
                rootView.setTextViewText(R.id.position_in_days, "%s days".format(DecimalRenderer.fromDouble(progress.positionInDays)))

                val projections = runningGoal.projection
                if (projections != null) {
                    rootView.setTextViewText(R.id.project_distance_per_day, "%s km/day".format(DecimalRenderer.fromDouble(projections.distancePerDay)))
                }
            }
        }

        // ENTIRE Widget CATCHES CLICK AND FIRES "edit" INTENT
        // Create the "edit" Intent to launch Activity
        val intent = Intent(context, GoalActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, runningGoal.id)
        val pendingIntentEdit = PendingIntent.getActivity(context, 0, intent, 0)
        rootView.setOnClickPendingIntent(R.id.widget_root, pendingIntentEdit)

        // Create an Intent to change Goal's GoalViewType
        rootView.setOnClickPendingIntent(R.id.btn_flip, getPendingSelfIntent(context, FLIP_CLICKED, runningGoal.id))
    }

    private fun getPendingSelfIntent(context: Context, action: String, appWidgetId: Int): PendingIntent {
        val intent = Intent(context, RunningGoalWidgetProvider::class.java)
        intent.action = action
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }
}

object ProgressRenderer {

    private const val gapAngle = 30f
    private const val startAngle = 90f + gapAngle
    private const val fullSweep = 360f - (gapAngle * 2)

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

        val max = runningGoal.target.distance
        val expected = runningGoal.progress.distanceExpected
        val current = runningGoal.progress.distanceToday.toInt()

        notchesSpecs = Notches(
                days = runningGoal.progress.daysTotal,
                notches = runningGoal.progress.daysTotal - 1,
                fullAngle = fullSweep
        )

        circleBase(context, canvas, paint)
        arcBase(context, canvas)
        arcCurrent(context, canvas, current.toFloat() / max)
        arcProgressNotches(context, canvas)

        //Draw text value
        val centreWidth = (bitmap.width / 2).toFloat()
        val centreHeight = (bitmap.height - textPaint.ascent()) / 2
//        val alignBottom = (bitmap.height - (stroke + 0))
        //canvas.drawText( "%s%%".format(percentage), centreWidth, centreHeight, textPaint)
        //Draw widget title.
        //textPaint.setTextSize((context.getResources().getDimension(R.dimen.widget_text_large_title) / density) as Int)
        canvas.drawText("%s".format(current), centreWidth, centreHeight, textPaint)

        textPaint.textSize = (context.resources.getDimensionPixelSize(R.dimen.widget_text_small_value)).toFloat()
        canvas.drawText("%s".format(expected.toInt()), centreWidth, centreHeight - textPaint.ascent() * 1.4f, textPaint)

        rootView.setImageViewBitmap(R.id.progress_view, bitmap)

        val factor = expected.toFloat() / max
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
        drawArc(context, canvas, Color.argb(150, 0, 255, 0), arcLengthAngle = fullSweep * percentage)
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
            Log.d("WIDGET", "arcProgressNotches | notch=%s correctedArcStartAngle=%s sweep=%s".format(it, correctedArcStartAngle, notchesSpecs.notchOffsetAngle))

            val notchScale = if ((it + 0) % 5 == 0) 0.5f else 0.2f // every 5th notch is longer
            drawArc(context, canvas, colour, correctedArcStartAngle, notchesSpecs.notchWidth, notchScale = notchScale)
        }
    }

    private fun getArcRect(stroke: Float, padding: Int, width: Int? = null, height: Int? = null): RectF {
        val arcWidth = width ?: widthMax
        val arcHeight = height ?: heightMax
        Log.d("getArcRect", "width=%s height=%s".format(arcWidth, arcHeight))
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

object DateRenderer {
    fun asFullDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("dd MMM"))
    }
}

object DecimalRenderer {
    fun fromDouble(value: Double): String {
        val numberFormat = NumberFormat.getInstance(Locale.ENGLISH)
        numberFormat.maximumFractionDigits = 1
        numberFormat.roundingMode = RoundingMode.HALF_UP

        return numberFormat.format(value)
    }
}

//fun Bitmap.rotate(angle: Float): Bitmap {
//    val source = this
//    val matrix = Matrix()
//    matrix.postRotate(angle)
//    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
//}
