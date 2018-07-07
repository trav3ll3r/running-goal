package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.SyncSourceDao
import au.com.beba.runninggoal.persistence.SyncSourceEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.experimental.CoroutineContext


class SyncSourceRepo private constructor(
        private val coroutineContext: CoroutineContext,
        private val syncSourceDao: SyncSourceDao) : SyncSourceRepository {

    companion object {
        private val TAG = SyncSourceRepo::class.java.simpleName
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

    override suspend fun getSyncSources(): List<SyncSource> = withContext(coroutineContext) {
        val entities = syncSourceDao.getAll()
        val sources = entities.map { entity2model(it) }.toList()
        Log.d(TAG, "syncSources=%s".format(sources.size))
        sources
    }

    override suspend fun getSyncSourceForType(type: String): SyncSource = withContext(coroutineContext) {
        val entity = syncSourceDao.getForType(type)
        entity2model(entity)
    }

    override suspend fun getDefaultSyncSource(): SyncSource = withContext(coroutineContext) {
        val entity = syncSourceDao.getDefault()
        entity2model(entity)
    }

    override suspend fun save(syncSource: SyncSource) = withContext(coroutineContext) {
        val syncEntity = SyncSourceEntity(
                syncSource.id,
                syncSource.type,
                syncSource.accessToken,
                syncSource.isDefault,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )
        val id: Long = syncSourceDao.insert(syncEntity)
        if (id < 0L) {
            syncSourceDao.update(syncEntity)
        }
    }

    private fun entity2model(entity: SyncSourceEntity?): SyncSource {
        return if (entity != null) {
            SyncSource(
                    entity.uid,
                    entity.type,
                    entity.accessToken,
                    entity.isActive,
                    LocalDateTime.ofEpochSecond(entity.syncedAt, 0, ZoneOffset.UTC)
            )
        } else {
            SyncSource()
        }
    }
}