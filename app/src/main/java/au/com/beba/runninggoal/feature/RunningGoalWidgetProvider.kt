package au.com.beba.runninggoal.feature

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.launchSilent
import au.com.beba.runninggoal.repo.GoalRepo


open class RunningGoalWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = RunningGoalWidgetProvider::class.java.simpleName
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        Log.i(TAG, "onUpdate")

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach {
            Log.d(TAG, "onUpdate | appWidgetId=%s".format(it))
            bindRemoteViews(context, appWidgetManager, it)
        }
    }

    private fun bindRemoteViews(context: Context, appWidgetManager: AppWidgetManager?, appWidgetId: Int) {
        Log.i(TAG, "bindRemoteViews")
        val goalRepo = GoalRepo.getInstance(context)
        // Get the layout for the App Widget and attach an on-click listener to the button
        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)

        launchSilent {
            Log.d(TAG, "bindRemoteViews | appWidgetId=%s".format(appWidgetId))
            val goal = goalRepo.getGoalForWidget(appWidgetId)
            GoalWidgetRenderer.updateUi(context, rootView, goal)
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager?.updateAppWidget(appWidgetId, rootView)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        super.onReceive(context, intent)

        if (GoalWidgetRenderer.FLIP_CLICKED == intent.action) {
            val appWidgetId = getWidgetIdFromIntent(intent)
            Log.d(TAG, "onReceive | appWidgetId=%s".format(appWidgetId))
            if (appWidgetId > 0) {
                val goalRepo = GoalRepo.getInstance(context)
                val appWidgetManager = AppWidgetManager.getInstance(context)

                launchSilent {
                    val goal = goalRepo.getGoalForWidget(appWidgetId)
                    if (goal != null) {

                        // UPDATE Goal's ViewType AND STORE IT IN DATABASE
                        goal.view.toggle()
                        goalRepo.save(goal, appWidgetId)

                        bindRemoteViews(context, appWidgetManager, appWidgetId)
                    }
                }
            }
        }
    }

    private fun getWidgetIdFromIntent(intent: Intent): Int {
        return intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
    }
}