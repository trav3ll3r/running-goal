package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.models.Widget
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.WidgetDao
import au.com.beba.runninggoal.persistence.WidgetEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.CoroutineContext


class WidgetRepo private constructor(
        private val coroutineContext: CoroutineContext,
        private val widgetDao: WidgetDao) : WidgetRepository {

    companion object {
        private val TAG = WidgetRepo::class.java.simpleName
        private lateinit var db: AppDatabase
        private var INSTANCE: WidgetRepository? = null

        @JvmStatic
        fun getInstance(context: Context, coroutineContext: CoroutineContext = DefaultDispatcher): WidgetRepository {
            if (INSTANCE == null) {
                synchronized(WidgetRepo::javaClass) {
                    db = AppDatabase.getInstance(context)
                    INSTANCE = WidgetRepo(coroutineContext, db.widgetDao())
                }
            }
            return INSTANCE!!
        }
    }

    override suspend fun getByWidgetId(widgetId: Int): Widget? {
        val entity = widgetDao.getByWidgetId(widgetId)
        return entity2model(entity)
    }

    override suspend fun getAllForGoal(goalId: Long): List<Widget> {
        Log.i(TAG, "getAllForGoal")
        val entities = widgetDao.getAllForGoal(goalId)
        val widgets: List<Widget> = entities.map { entity2model(it)!! }.toList()
        Log.d(TAG, "widgets=%s".format(widgets.size))
        return widgets
    }

    override suspend fun save(goalId: Long, appWidgetId: Int) = withContext(coroutineContext) {
        // FIND EXISTING
        var entity: WidgetEntity? = widgetDao.getByWidgetId(appWidgetId)

        if (entity == null) {
            entity = WidgetEntity(appWidgetId, goalId)
            widgetDao.insert(entity)
            Unit
        } else {
            if (entity.goalId != goalId) {
                entity.goalId = goalId
                widgetDao.update(entity)
            }
        }
    }

    private fun entity2model(entity: WidgetEntity?): Widget? {
        return if (entity != null) {
            Widget(entity.uid, entity.goalId)
        } else {
            null
        }
    }

    override suspend fun delete(widget: Widget): Int {
        Log.i(TAG, "delete")
        val widgetEntity = WidgetEntity(widget.id)
        val deletedRows = widgetDao.delete(widgetEntity)
        Log.d(TAG, "delete | widgets=%s".format(deletedRows))
        return deletedRows
    }
}