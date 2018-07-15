package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.launchSilent
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.WidgetRepository
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class GoalWidgetUpdater @Inject constructor(var goalRepo: GoalRepository, var widgetRepo: WidgetRepository) {

    companion object {
        private val TAG = GoalWidgetUpdater::class.java.simpleName
    }

    suspend fun updateAllWidgetsForGoal(context: Context, runningGoal: RunningGoal) {
        Log.i(TAG, "updateAllWidgetsForGoal")

        withContext(DefaultDispatcher) {
            val widgets = widgetRepo.getAllForGoal(runningGoal.id)
            Log.d(TAG, "updateAllWidgetsForGoal | widgets=%s".format(widgets.size))

            val widgetIds: IntArray = widgets.map { it.id }.toIntArray()
            Log.d(TAG, "updateAllWidgetsForGoal | widgetIds=%s".format(widgetIds.joinToString(", ")))

//            val rootView = RemoteViews(context.packageName, R.layout.goal_widget)
//            widgets.forEach { widget ->
//                GoalWidgetRenderer.updateUi(context, rootView, runningGoal, widget)
//            }
//            triggerWidgetUpdate(context, widgetIds)

            widgets.forEach { widget ->
                refreshWidget(context, widget.id)
            }

        }
    }

    fun refreshWidget(context: Context, appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateWidgetView(context, appWidgetManager, appWidgetId)
    }

    private fun updateWidgetView(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        Log.i(TAG, "updateWidgetView")
        // Get the layout for the App Widget and attach an on-click listener to the button
        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)

        launchSilent {
            Log.d(TAG, "updateWidgetView | appWidgetId=%s".format(appWidgetId))
            val goal = getGoalForWidget(appWidgetId)
            if (goal != null) {
                Log.d(TAG, "updateWidgetView | appWidgetId=%s | goalId=%s".format(appWidgetId, goal.id))

                val widget = widgetRepo.getByWidgetId(appWidgetId)
                if (widget != null) {
                    GoalWidgetRenderer.updateUi(context, rootView, goal, widget)
                    // Tell the AppWidgetManager to perform an update on the current app widget
                    appWidgetManager.updateAppWidget(appWidgetId, rootView)
                }
            } else {
                Log.d(TAG, "updateWidgetView | appWidgetId=%s | goal not found".format(appWidgetId))
            }
        }
    }

    private fun triggerWidgetUpdate(context: Context, widgetIds: IntArray) {
        Log.i(TAG, "triggerWidgetUpdate")
        val intent = Intent(context, RunningGoalWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        val componentName = ComponentName(context, RunningGoalWidgetProvider::class.java)
        val ids: IntArray = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName)
        val idsForUpdate = ids.filter { it in widgetIds }.toIntArray()
        Log.d(TAG, "triggerWidgetUpdate | idsForUpdate=%s".format(idsForUpdate.joinToString(", ")))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idsForUpdate)
        context.sendBroadcast(intent)
    }

    private suspend fun getGoalForWidget(widgetId: Int): RunningGoal? {
        val widget = widgetRepo.getByWidgetId(widgetId)
        if (widget != null) {
            return goalRepo.getById(widget.goalId)
        }
        return null
    }

//    private fun triggerWidgetUpdate(context: Context, widgetId: Int) {
//        val intent = Intent(context, RunningGoalWidgetProvider::class.java)
//        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
//        context.sendBroadcast(intent)
//    }

}