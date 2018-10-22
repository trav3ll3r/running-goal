package au.com.beba.runninggoal.repo.sync

import android.content.Context
import au.com.beba.runninggoal.goaldatabase.SyncSourceStorage
import au.com.beba.runninggoal.goaldatabase.syncsource.SyncSourceStorageImpl
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext


class SyncSourceRepo constructor(
        private val context: Context,
        private val coroutineContext: CoroutineContext = Dispatchers.Default) : SyncSourceRepository {

    companion object {
        private val TAG = SyncSourceRepo::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val syncSourceStorage: SyncSourceStorage by lazy {
        SyncSourceStorageImpl(context)
    }

    override suspend fun getSyncSources(): List<SyncSource> = withContext(coroutineContext) {
        logger.info("getSyncSources")
        syncSourceStorage.all()
    }

    override suspend fun getSyncSourceForType(type: String): SyncSource = withContext(coroutineContext) {
        logger.info("getSyncSourceForType")
        syncSourceStorage.allForType(type)
    }

    // TODO: SHOULD RETURN SyncSource?
    override suspend fun getDefaultSyncSource(): SyncSource = withContext(coroutineContext) {
        logger.info("getDefaultSyncSource")
        syncSourceStorage.default()
    }

    override suspend fun save(syncSource: SyncSource) = withContext(coroutineContext) {
        logger.info("save")
        syncSourceStorage.save(syncSource)
    }
}