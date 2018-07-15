package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import au.com.beba.runninggoal.launchSilent
import au.com.beba.runninggoal.models.Widget
import au.com.beba.runninggoal.repo.WidgetRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject


open class RunningGoalWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = RunningGoalWidgetProvider::class.java.simpleName
    }

    //    @Inject
//    lateinit var goalRepo: GoalRepository
    @Inject
    lateinit var widgetRepo: WidgetRepository
    @Inject
    lateinit var goalWidgetUpdater: GoalWidgetUpdater

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.i(TAG, "onUpdate")
        AndroidInjection.inject(this, context)

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach {
            Log.d(TAG, "onUpdate | appWidgetId=%s".format(it))
            goalWidgetUpdater.refreshWidget(context, it)
            //updateWidgetView(context, appWidgetManager, it)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)

        if (GoalWidgetRenderer.FLIP_CLICKED == intent.action) {
            val appWidgetId = getWidgetIdFromIntent(intent)
            Log.d(TAG, "onReceive | FLIP_CLICKED | appWidgetId=%s".format(appWidgetId))
            if (appWidgetId > 0) {
                launchSilent {
                    val widget = widgetRepo.getByWidgetId(appWidgetId)

                    if (widget != null) {
                        // UPDATE Widget's ViewType AND STORE IT IN DATABASE
                        widget.view.toggle()
                        widgetRepo.save(widget)

                        goalWidgetUpdater.refreshWidget(context, appWidgetId)
//                        val appWidgetManager = AppWidgetManager.getInstance(context)
//                        updateWidgetView(context, appWidgetManager, appWidgetId)
                    }
                }
            }
        }
    }

//    @Deprecated(message = "Call GoalWidgetUpdater.refreshWidget")
//    private fun updateWidgetView(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
//        Log.i(TAG, "updateWidgetView")
//        // Get the layout for the App Widget and attach an on-click listener to the button
//        val rootView = RemoteViews(context.packageName, R.layout.goal_widget)
//
//        launchSilent {
//            Log.d(TAG, "updateWidgetView | appWidgetId=%s".format(appWidgetId))
//            val goal = getGoalForWidget(appWidgetId)
//            if (goal != null) {
//                Log.d(TAG, "updateWidgetView | appWidgetId=%s | goalId=%s".format(appWidgetId, goal.id))
//
//                val widget = widgetRepo.getByWidgetId(appWidgetId)
//                if (widget != null) {
//                    GoalWidgetRenderer.updateUi(context, rootView, goal, widget)
//                    // Tell the AppWidgetManager to perform an update on the current app widget
//                    appWidgetManager.updateAppWidget(appWidgetId, rootView)
//                }
//            } else {
//                Log.d(TAG, "updateWidgetView | appWidgetId=%s | goal not found".format(appWidgetId))
//            }
//        }
//    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        Log.i(TAG, "onDeleted")
        super.onDeleted(context, appWidgetIds)

        launch {
            appWidgetIds?.forEach {
                widgetRepo.delete(Widget(it))
            }
        }
    }

//    private suspend fun getGoalForWidget(widgetId: Int): RunningGoal? {
//        val widget = widgetRepo.getByWidgetId(widgetId)
//        if (widget != null) {
//            return goalRepo.getById(widget.goalId)
//        }
//        return null
//    }

    private fun getWidgetIdFromIntent(intent: Intent): Int {
        return intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
    }
}