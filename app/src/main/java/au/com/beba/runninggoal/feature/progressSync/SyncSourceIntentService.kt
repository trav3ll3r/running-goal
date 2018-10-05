package au.com.beba.runninggoal.feature.progressSync

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import au.com.beba.runninggoal.feature.widget.GoalWidgetUpdater
import au.com.beba.runninggoal.domain.core.Distance
import au.com.beba.runninggoal.domain.core.GoalStatus
import au.com.beba.runninggoal.domain.core.RunningGoal
import au.com.beba.runninggoal.domain.sync.SyncSource
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.model.AthleteActivity
import au.com.beba.runninggoal.networking.source.SyncSourceProvider
import au.com.beba.runninggoal.repo.AthleteActivityRepository
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class SyncSourceIntentService : JobIntentService() {

    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository
    @Inject
    lateinit var syncSourceProvider: SyncSourceProvider

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var athleteActivityRepository: AthleteActivityRepository

    @Inject
    lateinit var goalWidgetUpdater: GoalWidgetUpdater

    companion object {
        private val TAG = SyncSourceIntentService::class.java.simpleName
        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"

        /**
         * Builds Intent for [SyncSourceIntentService] to update one or all eligible goals
         *
         * @param runningGoal NULL for all eligible goals or ONE specific goal
         */
        fun buildIntent(runningGoal: RunningGoal?): Intent {
            return Intent().apply {
                if (runningGoal != null) {
                    putExtra(EXTRA_GOAL_ID, runningGoal.id)
                }
            }
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

                val goals = getGoalsForUpdate(intent.getLongExtra(EXTRA_GOAL_ID, -1L))

                goals.forEach {
                    Log.d(TAG, "onHandleWork | syncGoalId=${it.id}")
                    goalRepository.markGoalUpdateStatus(true, it)

                    val workoutsFromSource = getAthleteActivitiesFromSource(it, syncSource)
                    persistWorkouts(it, workoutsFromSource)

                    // GET LATEST AND GREATEST FROM REPO
                    val workouts = athleteActivityRepository.getAllForGoal(it.id)
                    val distanceInMetre = totalDistanceForWorkouts(workouts)
                    if (distanceInMetre > -1f) {
                        updateGoalWithNewDistance(it, Distance.fromMetres(distanceInMetre), syncSource)
                        //TODO: NOTIFY athleteActivities UPDATED
                    }
                    goalRepository.markGoalUpdateStatus(false, it)
                    Log.d(TAG, "onHandleWork | distance=$distanceInMetre")
                }
            } else {
                Log.e(TAG, "onHandleWork | no Default Sync Source found")
            }
        }
    }

    private suspend fun getAthleteActivitiesFromSource(goal: RunningGoal, syncSource: SyncSource): List<AthleteActivity> {
        Log.i(TAG, "getAthleteActivitiesFromSource")

        syncSourceProvider.setSyncSourceProfile(ApiSourceProfile(syncSource.accessToken))
        return syncSourceProvider.getAthleteActivitiesForDateRange(
                goal.target.period.from.asEpochUtc(),
                goal.target.period.to.asEpochUtc())
    }


    // SUPPORT LOGIC
    private suspend fun getGoalsForUpdate(singleGoalId: Long): List<RunningGoal> {
        val goals = if (singleGoalId > 0) {
            // ONLY UPDATE SINGLE (SPECIFIC) GOAL
            val goal = withContext(DefaultDispatcher) { goalRepository.getById(singleGoalId) }
            if (goal != null) {
                listOf(goal)
            } else {
                listOf()
            }
        } else {
            // UPDATE ALL ELIGIBLE GOALS
            withContext(DefaultDispatcher) { goalRepository.fetchGoals() }
        }
        return goals.filter { it.progress.status in listOf(GoalStatus.EXPIRED, GoalStatus.ONGOING) }
    }

    private suspend fun updateGoalWithNewDistance(runningGoal: RunningGoal, distance: Distance, syncSource: SyncSource) {
        Log.i(TAG, "updateGoalWithNewDistance")
        Log.d(TAG, "updateGoalWithNewDistance | newDistance=${distance.value}")
        runningGoal.progress.distanceToday = distance
        goalRepository.save(runningGoal)

        syncSourceRepository.save(syncSource)

        goalWidgetUpdater.updateAllWidgetsForGoal(this, runningGoal)
    }

    private suspend fun persistWorkouts(goal: RunningGoal, workouts: List<AthleteActivity>) {
        athleteActivityRepository.deleteAllForGoal(goal.id)
        val mappedWorkouts = workouts.map {
            au.com.beba.runninggoal.domain.core.Workout(
                    it.name,
                    it.description,
                    it.distanceInMetres.toLong(),
                    it.dateTime
            )
        }
        athleteActivityRepository.insertAll(goal.id, mappedWorkouts)
    }

    private fun totalDistanceForWorkouts(workouts: List<au.com.beba.runninggoal.domain.core.Workout>): Long {
        Log.v(TAG, "totalDistanceForWorkouts")
        val distanceMetre: Long = workouts.asSequence().map { it.distanceInMetres }.sum()
        Log.v(TAG, "totalDistanceForWorkouts | distanceMetre=$distanceMetre")
        return distanceMetre
    }

}