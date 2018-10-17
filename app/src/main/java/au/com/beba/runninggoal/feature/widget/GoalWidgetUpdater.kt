package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.goal.GoalRepository
import au.com.beba.runninggoal.repo.widget.WidgetRepository
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject


class GoalWidgetUpdater
@Inject constructor(
        private var goalRepo: GoalRepository,
        var widgetRepo: WidgetRepository) {

    suspend fun updateAllWidgetsForGoal(context: Context, runningGoal: RunningGoal) {
        Timber.i("updateAllWidgetsForGoal")

        launch {
            val widgets = widgetRepo.getAllForGoal(runningGoal.id)
            Timber.d("updateAllWidgetsForGoal | widgets=%s".format(widgets.size))

            val widgetIds: IntArray = widgets.map { it.id }.toIntArray()
            Timber.d("updateAllWidgetsForGoal | widgetIds=%s".format(widgetIds.joinToString(", ")))

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
        Timber.i("updateWidgetView")
        // Get the layout for the App Widget and attach an on-click listener to the button
        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)

        launch {
            Timber.d("updateWidgetView | appWidgetId=%s".format(appWidgetId))
            val goal = getGoalForWidget(appWidgetId)
            if (goal == null) {
                Timber.d("updateWidgetView | appWidgetId=%s | goal not found".format(appWidgetId))
            }

            Timber.d("updateWidgetView | appWidgetId=%s | goalId=%s".format(appWidgetId, goal?.id))

            val widget = widgetRepo.getByWidgetId(appWidgetId)
            if (widget != null) {
                GoalWidgetRenderer.updateUi(context, rootView, goal, widget)
                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, rootView)
            }
        }
    }

    private suspend fun getGoalForWidget(widgetId: Int): RunningGoal? {
        val widget = widgetRepo.getByWidgetId(widgetId)
        if (widget != null) {
            return goalRepo.getById(widget.goalId)
        }
        return null
    }
}