package au.com.beba.runningoal.repo.widget

import android.content.Context
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.widget.Widget
import au.com.beba.runninggoal.feature.appevents.WidgetChangeEvent
import au.com.beba.runninggoal.feature.appevents.WidgetDeleteEvent
import au.com.beba.runninggoal.goaldatabase.WidgetStorage
import au.com.beba.runninggoal.goaldatabase.widget.WidgetStorageImpl
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext


class WidgetRepo constructor(
        private val context: Context,
        private val coroutineContext: CoroutineContext = Dispatchers.Default) : WidgetRepository {

    companion object {
        private val TAG = WidgetRepo::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)
    private val eventCentre: PublisherEventCentre

    init {
        eventCentre = EventCentre
    }

    private val widgetStorage: WidgetStorage by lazy {
        WidgetStorageImpl(context)
    }

    override suspend fun getById(widgetId: Int): Widget? = withContext(coroutineContext) {
        logger.info("getById")
        widgetStorage.getId(widgetId)
    }

    override suspend fun getAllForGoal(goalId: Long): List<Widget> = withContext(coroutineContext) {
        logger.info("getAllForGoal")
        widgetStorage.allForGoal(goalId)
    }

    override suspend fun save(widget: Widget) = withContext(coroutineContext) {
        logger.info("save")
        widgetStorage.save(widget)
        eventCentre.publish(WidgetChangeEvent(widget.id.toLong(), widget.goalId))
    }

    override suspend fun delete(widgetId: Int): Int = withContext(coroutineContext) {
        logger.info("delete")
        val count = widgetStorage.delete(widgetId)
        eventCentre.publish(WidgetDeleteEvent(widgetId.toLong()))
        count
    }

    override suspend fun pairWithGoal(goalId: Long, appWidgetId: Int) = withContext(coroutineContext) {
        logger.info("pairWithGoal")
        widgetStorage.pairWithGoal(goalId, appWidgetId)
        eventCentre.publish(WidgetChangeEvent(appWidgetId.toLong(), goalId))
    }
}