package au.com.beba.runninggoal.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.GoalChangeEvent
import au.com.beba.runninggoal.domain.event.GoalDeleteEvent
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.domain.event.WidgetChangeEvent
import au.com.beba.runninggoal.domain.event.WidgetDeleteEvent
import au.com.beba.runninggoal.domain.event.WorkoutSyncEvent
import au.com.beba.runninggoal.domain.widget.Widget
import au.com.beba.runninggoal.repo.widget.WidgetRepo
import au.com.beba.runninggoal.repo.widget.WidgetRepository
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference


object WidgetFeatureImpl
    : WidgetFeature, Subscriber {

    private val TAG = WidgetFeatureImpl::class.java.simpleName
    private const val INVALID_ID = 0L

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private lateinit var widgetRepo: WidgetRepository

    private var bootstrapped: Boolean = false
    override var isSuspended = false
    override var isReady = !isSuspended

    private lateinit var eventCentre: SubscriberEventCentre
    private lateinit var appContext: WeakReference<Context> // APPLICATION CONTEXT

    override fun getById(widgetId: Int): Widget? {
        return runBlocking { widgetRepo.getByWidgetId(widgetId) }
    }

    override fun getAllForGoal(goalId: Long): List<Widget> {
        return runBlocking { widgetRepo.getAllForGoal(goalId) }
    }

    override fun save(widget: Widget) {
        runBlocking { widgetRepo.save(widget) }
    }

    override fun delete(widgetId: Int) {
        runBlocking { widgetRepo.delete(widgetId) }
    }

    override fun bootstrap(application: Context) {
        logger.info("bootstrap")
        if (!bootstrapped) {
            appContext = WeakReference(application.applicationContext)
            widgetRepo = WidgetRepo(application)

            eventCentre = EventCentre
            eventCentre.registerSubscriber(this, GoalChangeEvent::class)
            eventCentre.registerSubscriber(this, GoalDeleteEvent::class)
            eventCentre.registerSubscriber(this, WorkoutSyncEvent::class)
            eventCentre.registerSubscriber(this, WidgetChangeEvent::class)
            eventCentre.registerSubscriber(this, WidgetDeleteEvent::class)
            bootstrapped = true
        }
    }

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        logger.info("newEvent")
        logger.debug("newEvent | postbox=%s".format(postbox.get()?.describe()))
        val pb = postbox.get()
        val event = pb?.takeLast()

        val widgetId: Long = when (event) {
            is WidgetChangeEvent -> event.widgetId
            is WidgetDeleteEvent -> event.widgetId
            else -> INVALID_ID
        }

        if (widgetId > INVALID_ID) {
            logger.info("newEvent | refreshing widget")
            startRefreshSingleWidget(widgetId.toInt())
        } else {
            val goalId: Long = when (event) {
                is GoalChangeEvent -> event.goalId
                is GoalDeleteEvent -> event.goalId
                is WorkoutSyncEvent -> if (!event.isUpdating) event.goalId else INVALID_ID
                else -> {
                    super.newEvent(postbox)
                    INVALID_ID
                }
            }

            if (goalId > INVALID_ID) {
                logger.info("newEvent | refreshing widget")
                startRefreshAllForGoal(goalId)
            }
        }
    }

    private fun startRefreshAllForGoal(goalId: Long) {
        logger.info("startRefreshAllForGoal")
        launch {
            val widgets = widgetRepo.getAllForGoal(goalId)
            widgets.forEach {
                startRefreshSingleWidget(it.id)
            }
        }

    }

    private fun startRefreshSingleWidget(widgetId: Int) {
        logger.info("startRefreshSingleWidget")
        val appCtx = appContext.get()
        if (appCtx != null) {
            val intent = Intent(appCtx, RunningGoalWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            // Use an array and EXTRA_APPWIDGET_IDS
            // (instead of AppWidgetManager.EXTRA_APPWIDGET_ID)
            // Seems the AppWidgetManager.onUpdate() is only fired on that
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, IntArray(1) { widgetId })
            appCtx.sendBroadcast(intent)
        }
    }
}