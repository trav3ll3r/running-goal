package au.com.beba.runninggoal.feature

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.repo.GoalRepo


open class RunningGoalWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = RunningGoalWidgetProvider::class.java.simpleName
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate")

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach {
            Log.d(TAG, "onUpdate | appWidgetId=%s".format(it))
            bindRemoteViews(context, appWidgetManager, it)
        }
    }

    private fun bindRemoteViews(context: Context, appWidgetManager: AppWidgetManager?, appWidgetId: Int) {
        // Get the layout for the App Widget and attach an on-click listener to the button
        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)
        val goal = GoalRepo.getGoalForWidget(appWidgetId)
        if (goal != null) {
            GoalWidgetRenderer.updateUi(context, rootView, goal)
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager?.updateAppWidget(appWidgetId, rootView)
        } else {
            Log.e(TAG, "Goal for widget not found!!!")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        super.onReceive(context, intent)

        if (GoalWidgetRenderer.FLIP_CLICKED == intent.action) {
            val appWidgetId = getWidgetIdFromIntent(intent)
            Log.d(TAG, "onReceive | widgetId=%s".format(appWidgetId))
            if (appWidgetId > 0) {

                val appWidgetManager = AppWidgetManager.getInstance(context)


                val goal = GoalRepo.getGoalForWidget(appWidgetId)
                if (goal != null) {

                    // UPDATE Goal's ViewType AND STORE IT IN DATABASE
                    goal.view.toggle()
                    GoalRepo.save(goal, appWidgetId)

                    bindRemoteViews(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    private fun getWidgetIdFromIntent(intent: Intent): Int {
        return intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
    }
}