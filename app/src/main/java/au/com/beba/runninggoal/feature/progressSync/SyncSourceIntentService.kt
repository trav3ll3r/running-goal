package au.com.beba.runninggoal.feature.progressSync

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.GoalStatus
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.source.SyncSourceProvider
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class SyncSourceIntentService : JobIntentService() {

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository
    @Inject
    lateinit var syncSourceProvider: SyncSourceProvider

    companion object {
        private val TAG = SyncSourceIntentService::class.java.simpleName

        fun buildIntent(): Intent {
            return Intent()
        }

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent, jobId: Int) {
            Log.d(TAG, "enqueueWork")
            enqueueWork(context, SyncSourceIntentService::class.java, jobId, work)
        }
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleWork(intent: Intent) {
        Log.i(TAG, "onHandleWork")

        launch {

            val syncSource = syncSourceRepository.getDefaultSyncSource()
            if (syncSource.isDefault) {
                Log.d(TAG, "onHandleWork | syncType=${syncSource.type}")

                val goals = getGoalForUpdate()

                goals.forEach {
                    Log.d(TAG, "onHandleWork | syncGoalId=${it.id}")
                    goalRepository.markGoalUpdateStatus(true, it)

                    val distanceInMetre = getDistanceFromSource(it, syncSource)
                    if (distanceInMetre > -1f) {
                        updateGoalWithNewDistance(it, Distance.fromMetres(distanceInMetre), syncSource)
                    }
                    goalRepository.markGoalUpdateStatus(false, it)
                    Log.d(TAG, "onHandleWork | distance=$distanceInMetre")
                }
            } else {
                Log.e(TAG, "onHandleWork | no Default Sync Source found")
            }
        }
    }

    private suspend fun getDistanceFromSource(goal: RunningGoal, syncSource: SyncSource): Float {
        Log.i(TAG, "getDistanceFromSource")

        syncSourceProvider.setSyncSourceProfile(ApiSourceProfile(syncSource.accessToken))
        return syncSourceProvider.getDistanceForDateRange(goal.target.period.from, goal.target.period.to)
    }

    private suspend fun getGoalForUpdate(): List<RunningGoal> {
        val goals = withContext(DefaultDispatcher) { goalRepository.fetchGoals() }
        return goals.filter { it.progress.status in listOf(GoalStatus.NOT_STARTED, GoalStatus.ONGOING) }
    }

    private suspend fun updateGoalWithNewDistance(goal: RunningGoal, distance: Distance, syncSource: SyncSource) {
        Log.i(TAG, "updateGoalWithNewDistance")
        Log.i(TAG, "updateGoalWithNewDistance | newDistance=${distance.value}")
        goal.progress.distanceToday = distance
        goalRepository.save(goal, goal.id)

        syncSourceRepository.save(syncSource)
    }
}