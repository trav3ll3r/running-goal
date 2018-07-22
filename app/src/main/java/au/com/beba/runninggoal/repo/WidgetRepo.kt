package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.models.Widget
import au.com.beba.runninggoal.models.WidgetView
import au.com.beba.runninggoal.models.WidgetViewType
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.WidgetDao
import au.com.beba.runninggoal.persistence.WidgetEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.runBlocking
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
        Log.v(TAG, "getAllForGoal | goalId=%s".format(goalId))
        val entities = widgetDao.getAllForGoal(goalId)
        val widgets: List<Widget> = entities.map { entity2model(it)!! }.toList()
        Log.d(TAG, "widgets=%s".format(widgets.size))
        return widgets
    }

    override suspend fun pairWithGoal(goalId: Long, appWidgetId: Int) = withContext(coroutineContext) {
        Log.i(TAG, "pairWithGoal")
        if (appWidgetId > 0) {
            runBlocking {
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

        Unit
    }

    override suspend fun save(widget: Widget) {
        Log.i(TAG, "save")

        // FIND EXISTING
        val entity: WidgetEntity? = widgetDao.getByWidgetId(widget.id)
        if (entity != null) {
            entity.viewType = widget.view.viewType.asDbValue()
            Log.d(TAG, "save | update")
            Log.d(TAG, "save | update | uid=%s, distance=%s".format(entity.uid, entity.viewType))
            widgetDao.update(entity)
        }
    }

    override suspend fun delete(widget: Widget): Int {
        Log.i(TAG, "delete")
        val widgetEntity = WidgetEntity(widget.id)
        val deletedRows = widgetDao.delete(widgetEntity)
        Log.d(TAG, "delete | widgets=%s".format(deletedRows))
        return deletedRows
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