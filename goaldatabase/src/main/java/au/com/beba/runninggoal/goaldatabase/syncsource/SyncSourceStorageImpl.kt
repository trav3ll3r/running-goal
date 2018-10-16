package au.com.beba.runninggoal.goaldatabase.syncsource

import android.content.Context
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.goaldatabase.AppDatabase
import au.com.beba.runninggoal.goaldatabase.SyncSourceStorage
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.experimental.coroutineContext


class SyncSourceStorageImpl(private val context: Context)
    : SyncSourceStorage {

    companion object {
        private val TAG = SyncSourceStorageImpl::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val syncSourceDao: SyncSourceDao by lazy {
        AppDatabase.getInstance(context).syncSourceDao()
    }

    override suspend fun all(): List<SyncSource> = withContext(coroutineContext) {
        logger.info("all")
        val entities = syncSourceDao.getAll()
        val sources = entities.asSequence().map { entity2model(it) }.toList()
        logger.debug("syncSources=%s".format(sources.size))
        sources
    }

    override suspend fun allForType(type: String): SyncSource = withContext(coroutineContext) {
        logger.info("allForType")
        val entity = syncSourceDao.getForType(type)
        entity2model(entity)
    }

    override suspend fun default(): SyncSource = withContext(coroutineContext) {
        logger.info("default")
        val entity = syncSourceDao.getDefault()
        entity2model(entity)
    }

    override suspend fun save(syncSource: SyncSource) = withContext(coroutineContext) {
        logger.info("save")
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