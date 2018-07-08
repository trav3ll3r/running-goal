package au.com.beba.runninggoal.feature.progressSync

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.source.SyncSourceProvider
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject


class ApiSourceIntentService : JobIntentService() {

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository
    @Inject
    lateinit var syncSourceProvider: SyncSourceProvider

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    companion object {
        private val TAG = ApiSourceIntentService::class.java.simpleName

        private const val SYNC_GOAL_ID = "SYNC_GOAL_ID"

        fun buildIntent(goalId: Int): Intent {
            val serviceIntent = Intent()
            serviceIntent.putExtra(SYNC_GOAL_ID, goalId)
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
        Log.i(TAG, "onHandleWork")

        launch {
            val syncGoalId = intent.getIntExtra(SYNC_GOAL_ID, 0)
            Log.d(TAG, "onHandleWork | syncGoalId=$syncGoalId")

            val goal = goalRepository.getGoalForWidget(syncGoalId)
            goalRepository.markGoalUpdateStatus(true, goal)
            val syncSource = syncSourceRepository.getDefaultSyncSource()
            if (syncSource.isDefault) {
                Log.d(TAG, "onHandleWork | syncType=${syncSource.type}")

                val distanceInMetre = getDistanceFromSource(goal, syncSource)
                if (distanceInMetre > -1f) {
                    updateGoalWithNewDistance(goal, Distance.fromMetres(distanceInMetre), syncSource)
                }
                goalRepository.markGoalUpdateStatus(false, goal)
                Log.d(TAG, "onHandleWork | distance=$distanceInMetre")
            } else {
                Log.e(TAG, "onHandleWork | no Default Sync Source found")
            }
        }
    }

    private suspend fun getDistanceFromSource(goal: RunningGoal, syncSource: SyncSource): Float {
        Log.i(TAG, "getDistanceFromSource")

        syncSourceProvider.setSyncSourceProfile(ApiSourceProfile(syncSource.accessToken))
        return syncSourceProvider.getDistanceForDateRange(goal.target.start, goal.target.end)
    }

    private suspend fun updateGoalWithNewDistance(goal: RunningGoal, distance: Distance, syncSource: SyncSource) {
        Log.i(TAG, "updateGoalWithNewDistance")
        Log.i(TAG, "updateGoalWithNewDistance | newDistance=${distance.value}")
        goal.progress.distanceToday = distance
        goalRepository.save(goal, goal.id)

        syncSourceRepository.save(syncSource)
    }
}