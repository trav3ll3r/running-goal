package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal


object GoalWidgetUpdater {

    private val TAG = GoalWidgetUpdater::class.java.simpleName

    fun updateAllWidgetsForGoal(context: Context, runningGoal: RunningGoal) {
        Log.i(TAG, "updateAllWidgetsForGoal")

        val appWidgetManager = AppWidgetManager.getInstance(context)

        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)

        GoalWidgetRenderer.updateUi(context, rootView, runningGoal)

        val appWidgetId = runningGoal.id
        Log.d(TAG, "updateAllWidgetsForGoal | appWidgetId=%s".format(appWidgetId))
        appWidgetManager.updateAppWidget(appWidgetId, rootView)
    }

}