package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.JobIntentService
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import au.com.beba.runningoal.repo.widget.WidgetRepo
import au.com.beba.runningoal.repo.widget.WidgetRepository
import kotlinx.coroutines.experimental.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory


internal class WidgetUpdateIntentService
    : JobIntentService() {

    companion object {
        private val TAG = WidgetUpdateIntentService::class.java.simpleName

        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"
        private const val EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID"

        fun updateWidget(context: Context, widgetId: Int, jobId: Int = 1001) {
            val work = Intent().apply {
                putExtra(EXTRA_WIDGET_ID, widgetId)
            }
            enqueueWork(context, WidgetUpdateIntentService::class.java, jobId, work)
        }
    }

    private val widgetRepo: WidgetRepository
    private val goalRepo: GoalRepository
    private val eventCentre: SubscriberEventCentre
    private val logger: Logger = LoggerFactory.getLogger(TAG)

    init {
        widgetRepo = WidgetRepo(this)
        goalRepo = GoalRepo(this)
        eventCentre = EventCentre
    }

    override fun onHandleWork(intent: Intent) {
        logger.info("onHandleWork")

        val goalId = intent.getLongExtra(EXTRA_GOAL_ID, -1L)
        val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)

        when {
            (goalId > 0) -> updateAllWidgetsForGoal(goalId)
            (widgetId > 0) -> refreshWidget(widgetId)
        }
    }

    private fun updateAllWidgetsForGoal(goalId: Long) {
        logger.info("updateAllWidgetsForGoal")

        launch {
            val widgets = widgetRepo.getAllForGoal(goalId)
            logger.debug("updateAllWidgetsForGoal | widgets=%s".format(widgets.size))

            val widgetIds: IntArray = widgets.map { it.id }.toIntArray()
            logger.debug("updateAllWidgetsForGoal | widgetIds=%s".format(widgetIds.joinToString(", ")))

            widgets.forEach { widget ->
                refreshWidget(widget.id)
            }
        }
    }

    private fun refreshWidget(appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        updateWidgetView(appWidgetManager, appWidgetId)
    }

    private fun updateWidgetView(appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        logger.info("updateWidgetView")
        // Get the layout for the App Widget and attach an on-click listener to the button
        val rootView = RemoteViews(packageName, R.layout.goal_widget)

        val context = this
        launch {
            logger.debug("updateWidgetView | appWidgetId=%s".format(appWidgetId))
            val goal = getGoalForWidget(appWidgetId)
            if (goal == null) {
                logger.debug("updateWidgetView | appWidgetId=%s | goal not found".format(appWidgetId))
            }

            logger.debug("updateWidgetView | appWidgetId=%s | goalId=%s".format(appWidgetId, goal?.id))

            val widget = widgetRepo.getById(appWidgetId)
            if (widget != null) {
                GoalWidgetRenderer().updateUi(context, rootView, goal, widget)
                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, rootView)
            }
        }
    }

    private suspend fun getGoalForWidget(widgetId: Int): RunningGoal? {
        val widget = widgetRepo.getById(widgetId)
        if (widget != null) {
            return goalRepo.getById(widget.goalId)
        }
        return null
    }
}