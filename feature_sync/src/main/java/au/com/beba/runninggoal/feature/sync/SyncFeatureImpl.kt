package au.com.beba.runninggoal.feature.sync

import android.content.Context
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.repo.sync.SyncSourceRepo
import au.com.beba.runninggoal.repo.sync.SyncSourceRepository
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object SyncFeatureImpl : SyncFeature {

    private val TAG = SyncFeatureImpl::class.java.simpleName

    private lateinit var syncSourceRepository: SyncSourceRepository

    private val defaultSyncSource: SyncSource? by lazy {
        runBlocking { syncSourceRepository.getDefaultSyncSource() }
    }
    private val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun bootstrap(application: Context) {
        syncSourceRepository = SyncSourceRepo(application)
    }

    override val isSuspended: Boolean = false

    override val isReady: Boolean
        get() {
            return defaultSyncSource != null
        }

    override fun getSyncSourceForType(syncSourceType: String): SyncSource {
        return runBlocking { syncSourceRepository.getSyncSourceForType(syncSourceType) }
    }

    override fun getAllConfiguredSources(): List<SyncSource> {
        return runBlocking { syncSourceRepository.getSyncSources() }
    }

    override fun storeSyncSource(syncSource: SyncSource) {
        //TODO:
    }

    override fun syncNow(context: Context, goalId: Long?, jobId: Int) {
        logger.info("syncNow")
        launch {
            val syncSource = syncSourceRepository.getDefaultSyncSource()
            if (syncSource.isDefault) {
                // Enqueues new JobIntentService
                SyncSourceIntentService.enqueueWork(
                        context,
                        SyncSourceIntentService.buildIntent(goalId),
                        jobId)
            } else {
                launch {
                    // NOTIFY USER ABOUT MISSING DEFAULT SYNC SOURCE
//                    val dialog = AlertDialog.Builder(ctx)
//                            .setCancelable(true)
//                            .setTitle(R.string.sync_now_error)
//                            .setMessage(R.string.message_no_default_sync_sources)
//                            .setPositiveButton(android.R.string.ok, null)
//                            .create()
//
//                    if (!isFinishing) {
//                        dialog.show()
//                    }
                }
            }
        }
    }
}