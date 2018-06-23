package au.com.beba.runninggoal.feature.progressSync

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.source.ApiSource
import au.com.beba.runninggoal.networking.source.StravaApiSource
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.time.LocalDate
import javax.inject.Inject


class ApiSourceIntentService : JobIntentService() {

    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    companion object {
        private val TAG = ApiSourceIntentService::class.java.simpleName

        private val SYNC_SOURCE_TYPE = "SYNC_SOURCE_TYPE"

        fun buildIntent(syncSourceType: String): Intent {
            val serviceIntent = Intent()
            serviceIntent.putExtra(SYNC_SOURCE_TYPE, syncSourceType)
            return serviceIntent
        }

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent, jobId: Int) {
            Log.d(TAG, "enqueueWork")
            enqueueWork(context, ApiSourceIntentService::class.java, jobId, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork")

        launch(UI) {
            val syncType = intent.getStringExtra(SYNC_SOURCE_TYPE)
            Log.d(TAG, "onHandleWork | syncType=$syncType")
            val syncSource = syncSourceRepository.getSyncSourceForType(syncType)

            val source: ApiSource = StravaApiSource(sourceProfile = ApiSourceProfile(syncSource.accessToken))
            val distance = source.getDistanceForDateRange(LocalDate.now(), LocalDate.now())
            Log.d(TAG, "onHandleWork | distance=$distance")
        }
    }
}