package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RunningGoalWidgetProvider
    : AppWidgetProvider() {

    companion object {
        private val TAG = RunningGoalWidgetProvider::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private var widgetFeature: WidgetFeature? = null

    private fun resolveDependencies(context: Context?) {
        if (context != null) {
            widgetFeature = WidgetFeatureImpl
            widgetFeature!!.bootstrap(context)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        logger.info("onUpdate")
        resolveDependencies(context)

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach {
            scheduleWidgetUpdate(context, it)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        logger.info("onReceive")
        super.onReceive(context, intent)
        resolveDependencies(context)

        logger.debug("onReceive | action=%s".format(intent.action))

        if (GoalWidgetRenderer.FLIP_CLICKED == intent.action) {
            val appWidgetId = getWidgetIdFromIntent(intent)
            logger.debug("onReceive | FLIP_CLICKED | appWidgetId=%s".format(appWidgetId))
            if (appWidgetId > 0) {

                val widget = widgetFeature?.getById(appWidgetId)

                if (widget != null) {
                    logger.info("onReceive | FLIP_CLICKED | widget found")
                    // UPDATE Widget's ViewType AND STORE IT IN DATABASE
                    widget.view.toggle()
                    widgetFeature?.save(widget)
                }
            }
        }
    }

    private fun getWidgetIdFromIntent(intent: Intent): Int {
        return intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
    }

    private fun scheduleWidgetUpdate(context: Context, appWidgetId: Int) {
        logger.info("scheduleWidgetUpdate")
        logger.debug("scheduleWidgetUpdate | appWidgetId=$appWidgetId")
        WidgetUpdateIntentService.updateWidget(context, appWidgetId)
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        logger.info("onAppWidgetOptionsChanged")
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        if (context != null) {
            logger.info("onAppWidgetOptionsChanged | updateWidget")
            scheduleWidgetUpdate(context, appWidgetId)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        logger.info("onDeleted")
        super.onDeleted(context, appWidgetIds)
        resolveDependencies(context)

        appWidgetIds?.forEach {
            widgetFeature?.delete(it)
        }
    }
}