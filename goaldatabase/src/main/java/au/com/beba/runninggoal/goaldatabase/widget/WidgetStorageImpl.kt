package au.com.beba.runninggoal.goaldatabase.widget

import android.content.Context
import au.com.beba.runninggoal.domain.widget.Widget
import au.com.beba.runninggoal.domain.widget.WidgetView
import au.com.beba.runninggoal.domain.widget.WidgetViewType
import au.com.beba.runninggoal.goaldatabase.AppDatabase
import au.com.beba.runninggoal.goaldatabase.WidgetStorage
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.coroutineContext

class WidgetStorageImpl(private val context: Context)
    : WidgetStorage {

    companion object {
        private val TAG = WidgetStorageImpl::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val widgetDao: WidgetDao by lazy {
        AppDatabase.getInstance(context).widgetDao()
    }

    override suspend fun getId(widgetId: Int): Widget? = withContext(coroutineContext) {
        logger.info("getId")
        val entity = widgetDao.getByWidgetId(widgetId)
        entity2model(entity)
    }

    override suspend fun allForGoal(goalId: Long): List<Widget> = withContext(coroutineContext) {
        logger.info("getAllForGoal")
        logger.debug("getAllForGoal | goalId=%s".format(goalId))
        val entities = widgetDao.getAllForGoal(goalId)
        val widgets: List<Widget> = entities.map { entity2model(it)!! }.toList()
        logger.debug("widgets=%s".format(widgets.size))
        widgets

    }

    override suspend fun pairWithGoal(goalId: Long, appWidgetId: Int) = withContext(coroutineContext) {
        logger.info(TAG, "pairWithGoal")
        if (appWidgetId > 0) {
            // FIND EXISTING
            var entity: WidgetEntity? = widgetDao.getByWidgetId(appWidgetId)

            if (entity == null) {
                entity = WidgetEntity(appWidgetId, goalId)
                widgetDao.insert(entity)
            } else {
                if (entity.goalId != goalId) {
                    entity.goalId = goalId
                    widgetDao.update(entity)
                } else {
                    Unit
                }
            }
        }
    }

    override suspend fun save(widget: Widget) = withContext(coroutineContext) {
        logger.info("save")

        // FIND EXISTING
        val entity: WidgetEntity? = widgetDao.getByWidgetId(widget.id)
        if (entity != null) {
            entity.viewType = widget.view.viewType.asDbValue()
            logger.debug("save | update")
            logger.debug("save | update | uid=%s, distance=%s".format(entity.uid, entity.viewType))
            widgetDao.update(entity)
        }
    }

    override suspend fun delete(widget: Widget): Int = withContext(coroutineContext) {
        logger.info("delete")
        val widgetEntity = WidgetEntity(widget.id)
        val deletedRows = widgetDao.delete(widgetEntity)
        logger.debug("delete | widgets=%s".format(deletedRows))
        deletedRows
    }

    private fun entity2model(entity: WidgetEntity?): Widget? {
        return if (entity != null) {
            Widget(
                    entity.uid,
                    entity.goalId,
                    WidgetView(WidgetViewType.fromDbValue(entity.viewType)))
        } else {
            null
        }
    }
}