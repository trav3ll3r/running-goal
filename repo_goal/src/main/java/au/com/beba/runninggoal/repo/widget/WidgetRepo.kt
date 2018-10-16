package au.com.beba.runninggoal.repo.widget

import android.content.Context
import au.com.beba.runninggoal.domain.widget.Widget
import au.com.beba.runninggoal.goaldatabase.WidgetStorage
import au.com.beba.runninggoal.goaldatabase.widget.WidgetStorageImpl
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext


class WidgetRepo constructor(
        private val context: Context,
        private val coroutineContext: CoroutineContext = DefaultDispatcher) : WidgetRepository {

    companion object {
        private val TAG = WidgetRepo::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val widgetStorage: WidgetStorage by lazy {
        WidgetStorageImpl(context)
    }

    override suspend fun getByWidgetId(widgetId: Int): Widget? = withContext(coroutineContext) {
        logger.info("getByWidgetId")
        widgetStorage.getId(widgetId)
    }

    override suspend fun getAllForGoal(goalId: Long): List<Widget> = withContext(coroutineContext) {
        logger.info("getAllForGoal")
        widgetStorage.allForGoal(goalId)
    }

    override suspend fun pairWithGoal(goalId: Long, appWidgetId: Int) = withContext(coroutineContext) {
        logger.info("pairWithGoal")
        widgetStorage.pairWithGoal(goalId, appWidgetId)
    }

    override suspend fun save(widget: Widget) = withContext(coroutineContext) {
        logger.info("save")
        widgetStorage.save(widget)
    }

    override suspend fun delete(widget: Widget): Int = withContext(coroutineContext) {
        logger.info("delete")
        widgetStorage.delete(widget)
    }
}