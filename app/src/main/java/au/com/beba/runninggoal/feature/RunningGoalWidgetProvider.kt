package au.com.beba.runninggoal.feature

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.repo.GoalRepo


class RunningGoalWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = RunningGoalWidgetProvider::class.java.simpleName
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate")
        val widgetCount = appWidgetIds.size

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (i in 0 until widgetCount) {
            val appWidgetId = appWidgetIds[i]
            Log.d(TAG, "onUpdate | appWidgetId=%s".format(appWidgetId))

            // Create an Intent to launch GoalActivity
            val intent = Intent(context, GoalActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val rootView = RemoteViews(context.packageName, R.layout.goal_widget)
            val goal = GoalRepo.getGoalForWidget(appWidgetId)
            if (goal != null) {
                GoalWidgetUtil.updateUi(rootView, goal)
                rootView.setOnClickPendingIntent(R.id.btn_configure, pendingIntent)

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager?.updateAppWidget(appWidgetId, rootView)
            } else {
                Log.e(TAG, "Goal for widget not found!!!")
            }
        }
    }
}