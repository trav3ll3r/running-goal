package au.com.beba.runninggoal.feature

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Paint.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.view.View
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.GoalViewType
import au.com.beba.runninggoal.models.RunningGoal
import java.math.RoundingMode
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

    fun render(context: Context, rootView: RemoteViews, runningGoal: RunningGoal) {
        val width = 400
        val height = 400
//        val stroke = 50.0f

        //Paint for arc stroke.
        val paint = Paint(FILTER_BITMAP_FLAG or DITHER_FLAG or ANTI_ALIAS_FLAG)
//        paint.strokeWidth = stroke
//        paint.strokeCap = Paint.Cap.BUTT
        paint.strokeCap = Paint.Cap.ROUND

        //Paint for text values.
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = (context.resources.getDimensionPixelSize(R.dimen.widget_text_large_value)).toFloat()
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val max = runningGoal.target.distance
        val expected = runningGoal.progress.distanceExpected
        val current = runningGoal.progress.distanceToday.toInt()

        circleBase(context, canvas, paint)
        arcBase(context, canvas, paint)
        arcCurrent(context, canvas, paint, current.toFloat() / max)

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
        val notchAngle = gapAngle + (factor * fullSweep)
        ProgressRenderer.renderNotch(context, rootView, notchAngle)
    }

    private fun renderNotch(context: Context, rootView: RemoteViews, angle: Float) {
        val notch = Icon.createWithResource(context, R.drawable.ic_navigation_24dp)
        val drawableNotch = notch.loadDrawable(context)

        if (drawableNotch != null) {
            val bitmapIcon = rotateNotch(drawableNotch, angle)
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
        canvas.drawCircle(arcRect.centerX(), arcRect.centerY(), 200f, paint)
    }

    private fun arcBase(context: Context, canvas: Canvas, paint: Paint) {
        //First draw full arc as background.
        paint.color = Color.argb(55, 255, 255, 255)
        val stroke = context.resources.getDimensionPixelSize(R.dimen.base_ring_stroke).toFloat()
        val padding = context.resources.getDimensionPixelSize(R.dimen.outer_ring_stroke)
        paint.strokeWidth = stroke
        paint.style = Paint.Style.STROKE
        val arcRect = getArcRect(stroke, padding)

        canvas.drawArc(arcRect, startAngle, fullSweep, false, paint)
    }

    private fun arcCurrent(context: Context, canvas: Canvas, paint: Paint, percentage: Float) {
        //Then draw arc progress with actual value.
        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        val stroke = context.resources.getDimensionPixelSize(R.dimen.inner_ring_stroke).toFloat()
        val padding = context.resources.getDimensionPixelSize(R.dimen.outer_ring_stroke)
        paint.strokeWidth = stroke
        val arcRect = getArcRect(stroke, padding)
        val angle = fullSweep * percentage
        canvas.drawArc(arcRect, startAngle, angle, false, paint)
    }

    private fun getArcRect(stroke: Float, padding: Int, width: Int = 400, height: Int = 400): RectF {
        val arcRect = RectF()
        arcRect.set((stroke / 2 + padding), (stroke / 2 + padding), (width - padding - stroke / 2), (height - padding - stroke / 2))
        return arcRect
    }

    private fun rotateNotch(drawable: Drawable, angle: Float): Bitmap {
        val max = 420
        val right = 235
        val iconSize = 50
        val bitmap = Bitmap.createBitmap(max, max, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // ROTATE AROUND THE CENTER OF CANVAS
        canvas.rotate(
                angle,
                canvas.width / 2f, // px, center x
                canvas.height / 2f // py, center y
        )
        drawable.setBounds(right - iconSize, max - iconSize, right, max)
        drawable.draw(canvas)
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

fun Bitmap.rotate(angle: Float): Bitmap {
    val source = this
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}
