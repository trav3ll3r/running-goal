package au.com.beba.runninggoal.feature

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
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
        rootView.setTextViewText(R.id.goal_period, "%s to %s".format(DateRenderer.asFullDate(runningGoal.target.start), DateRenderer.asFullDate(runningGoal.target.end)))

        when (runningGoal.view.viewType) {
            GoalViewType.PROGRESS_BAR -> {
                rootView.setViewVisibility(R.id.goal_in_visuals, View.VISIBLE)
                rootView.setViewVisibility(R.id.goal_in_numbers, View.GONE)
                rootView.setImageViewIcon(R.id.btn_flip, Icon.createWithResource(context, R.drawable.ic_list_24dp))

                rootView.setProgressBar(R.id.goal_progress_time    , runningGoal.target.distance, runningGoal.progress.distanceExpected.toInt(), false)
                rootView.setProgressBar(R.id.goal_progress_distance, runningGoal.target.distance, runningGoal.progress.distanceToday.toInt(), false)
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