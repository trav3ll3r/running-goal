package au.com.beba.runninggoal.feature.progressSync

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.source.ApiSource
import au.com.beba.runninggoal.networking.source.StravaApiSource
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject


class ApiSourceIntentService : JobIntentService() {

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    companion object {
        private val TAG = ApiSourceIntentService::class.java.simpleName

        private val SYNC_SOURCE_TYPE = "SYNC_SOURCE_TYPE"
        private val SYNC_GOAL_ID = "SYNC_GOAL_ID"

        fun buildIntent(goalId: Int, syncSourceType: String): Intent {
            val serviceIntent = Intent()
            serviceIntent.putExtra(SYNC_GOAL_ID, goalId)
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
        Log.i(TAG, "onHandleWork")

        launch {
            val syncGoalId = intent.getIntExtra(SYNC_GOAL_ID, 0)
            val syncType = intent.getStringExtra(SYNC_SOURCE_TYPE)
            Log.d(TAG, "onHandleWork | syncGoalId=$syncGoalId")
            Log.d(TAG, "onHandleWork | syncType=$syncType")

            val goal = goalRepository.getGoalForWidget(syncGoalId)
            val syncSource = syncSourceRepository.getSyncSourceForType(syncType)

            val distanceInMetre = getDistanceFromSource(goal, syncSource)
            if (distanceInMetre > -1f) {
                val df = DecimalFormat("0.#")
                df.roundingMode = RoundingMode.HALF_EVEN
                val roundedDistance = df.format(distanceInMetre / 1000).toFloat()
                updateGoalWithNewDistance(goal, Distance(roundedDistance), syncSource)
            }
            Log.d(TAG, "onHandleWork | distance=$distanceInMetre")
        }
    }

    private suspend fun getDistanceFromSource(goal: RunningGoal, syncSource: SyncSource): Float {
        Log.i(TAG, "getDistanceFromSource")

        val source: ApiSource = StravaApiSource(sourceProfile = ApiSourceProfile(syncSource.accessToken))
        return source.getDistanceForDateRange(goal.target.start, goal.target.end)
    }

    private suspend fun updateGoalWithNewDistance(goal: RunningGoal, distance: Distance, syncSource: SyncSource) {
        Log.i(TAG, "updateGoalWithNewDistance")
        goal.progress.distanceToday = distance
        goalRepository.save(goal, goal.id)

        syncSourceRepository.save(syncSource)
    }
}