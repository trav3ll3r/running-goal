package au.com.beba.runninggoal.feature.sync

import android.content.Context
import au.com.beba.runninggoal.domain.workout.sync.SourceType
import au.com.beba.runninggoal.domain.workout.sync.StravaSourceType
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.repo.sync.SyncSourceRepo
import au.com.beba.runninggoal.repo.sync.SyncSourceRepository
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object SyncFeatureImpl : SyncFeature {

    private val TAG = SyncFeatureImpl::class.java.simpleName

    private lateinit var syncSourceRepository: SyncSourceRepository

    override var defaultSyncSource: SyncSource? = null
        get() {
            return runBlocking { syncSourceRepository.getDefaultSyncSource() }
        }
    private val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun bootstrap(application: Context) {
        syncSourceRepository = SyncSourceRepo(application)
    }

    override var isSuspended: Boolean = false

    override var isReady: Boolean = false
        get() {
            return !isSuspended && defaultSyncSource != null
        }

    override fun getById(syncSourceId: Long): SyncSource? {
        return runBlocking { syncSourceRepository.getById(syncSourceId) }
    }

    override fun getSyncSourceTypes(): List<SourceType> {
        return listOf(
                StravaSourceType(R.drawable.ic_strava, R.string.source_strava)
        )
    }

    override fun getAllConfiguredSources(): List<SyncSource> {
        return runBlocking { syncSourceRepository.getSyncSources() }
    }

    override fun storeSyncSource(syncSource: SyncSource) {
        return runBlocking { syncSourceRepository.save(syncSource) }
    }

    override fun syncNow(context: Context, goalId: Long?) {
        logger.info("syncNow")
        SyncSourceIntentService.enqueueWork(context, goalId)
    }
}