package au.com.beba.runninggoal.repo

import android.content.Context
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.SyncSourceDao
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import java.time.LocalDate
import kotlin.coroutines.experimental.CoroutineContext


class SyncSourceRepo private constructor(
        private val coroutineContext: CoroutineContext,
        private val syncSourceDao: SyncSourceDao) : SyncSourceRepository {

    companion object {
        private lateinit var db: AppDatabase
        private var INSTANCE: SyncSourceRepository? = null

        @JvmStatic
        fun getInstance(context: Context, coroutineContext: CoroutineContext = DefaultDispatcher): SyncSourceRepository {
            if (INSTANCE == null) {
                synchronized(SyncSourceRepo::javaClass) {
                    db = AppDatabase.getInstance(context)
                    INSTANCE = SyncSourceRepo(coroutineContext, db.syncSourceDao())
                }
            }
            return INSTANCE!!
        }
    }

    override suspend fun getSyncSourceForType(type: String): SyncSource = withContext(coroutineContext) {
        val entity = syncSourceDao.getForType(type)

        val result: SyncSource
        result = if (entity != null) {
            SyncSource(
                    entity.uid,
                    entity.type,
                    entity.accessToken,
                    LocalDate.ofEpochDay(entity.syncedAt)
            )
        } else {
            SyncSource()
        }

        result
    }

//    override suspend fun save(goal: RunningGoal, appWidgetId: Int) = withContext(coroutineContext) {
//        val goalEntity = RunningGoalEntity(appWidgetId)
//        goalEntity.goalName = goal.name
//        goalEntity.targetDistance = goal.target.distance.value
//        goalEntity.currentDistance = goal.progress.distanceToday.value
//        goalEntity.startDate = goal.target.start.toEpochDay()
//        goalEntity.endDate = goal.target.end.toEpochDay()
//        goalEntity.widgetId = appWidgetId
//        goalEntity.viewType = goal.view.viewType.asDbValue()
//
//        val id: Long = runningGoalDao.insert(goalEntity)
//        if (id < 0L) {
//            runningGoalDao.update(goalEntity)
//        }
//    }
}