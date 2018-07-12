package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.WidgetRepository
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class GoalWidgetUpdater @Inject constructor(var widgetRepo: WidgetRepository) {

    companion object {
        private val TAG = GoalWidgetUpdater::class.java.simpleName
    }

    suspend fun updateAllWidgetsForGoal(context: Context, runningGoal: RunningGoal) {
        Log.i(TAG, "updateAllWidgetsForGoal")

        val appWidgetManager = AppWidgetManager.getInstance(context)

        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)

        GoalWidgetRenderer.updateUi(context, rootView, runningGoal)

        withContext(DefaultDispatcher) {
            val widgets = widgetRepo.getAllForGoal(runningGoal.id)

            widgets.forEach {
                Log.d(TAG, "updateAllWidgetsForGoal | appWidgetId=%s".format(it.id))
                appWidgetManager.updateAppWidget(it.id, rootView)
            }
        }

    }

}