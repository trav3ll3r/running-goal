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
        val entities: List<SyncSourceEntity> = syncSourceDao.getAll()
        logger.debug("syncSources=%s".format(entities.size))
        entities.toModels()
    }

    override suspend fun allForType(type: String): List<SyncSource> = withContext(coroutineContext) {
        logger.info("allForType")
        val entity = syncSourceDao.getForType(type)
        entity.toModels()
    }

    override suspend fun default(): SyncSource? = withContext(coroutineContext) {
        logger.info("default")
        val entity = syncSourceDao.getDefault()
        entity?.toModel()
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
}

private fun List<SyncSourceEntity>.toModels(): List<SyncSource> {
    return asSequence().map { it.toModel() }.toList()
}

private fun SyncSourceEntity.toModel(): SyncSource {
    return SyncSource(
            uid,
            type,
            accessToken,
            isActive,
            LocalDateTime.ofEpochSecond(syncedAt, 0, ZoneOffset.UTC)
    )
}