package au.com.beba.runninggoal.feature.sync

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import au.com.beba.runninggoal.domain.Distance
import au.com.beba.runninggoal.domain.GoalStatus
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.NoDefaultSyncSource
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.WorkoutSyncEvent
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import au.com.beba.runninggoal.repo.sync.SyncSourceRepo
import au.com.beba.runninggoal.repo.sync.SyncSourceRepository
import au.com.beba.runninggoal.repo.sync.providers.StravaSyncSourceProvider
import au.com.beba.runninggoal.repo.sync.providers.SyncSourceProvider
import au.com.beba.runninggoal.repo.workout.WorkoutRepo
import au.com.beba.runninggoal.repo.workout.WorkoutRepository
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory


internal class SyncSourceIntentService : JobIntentService() {

    private val syncSourceRepository: SyncSourceRepository
    private val goalRepository: GoalRepository
    private val workoutRepository: WorkoutRepository
    private val eventCentre: PublisherEventCentre

    companion object {
        private val TAG = SyncSourceIntentService::class.java.simpleName
        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"

        /**
         * Builds Intent for [SyncSourceIntentService] to update one or all eligible goals
         *
         * @param goalId NULL to sync all all eligible goals or ONE specific goal
         */
        private fun buildIntent(goalId: Long?): Intent {
            return Intent().apply {
                if (goalId != null) {
                    putExtra(EXTRA_GOAL_ID, goalId)
                }
            }
        }

        /**
         * Convenience method for enqueuing work into this service.
         */
        fun enqueueWork(context: Context, goalId: Long?, jobId: Int = 1000) {
            //logger.debug("enqueueWork")
            enqueueWork(context, SyncSourceIntentService::class.java, jobId, buildIntent(goalId))
        }
    }

    init {
        syncSourceRepository = SyncSourceRepo(this)
        goalRepository = GoalRepo(this)
        workoutRepository = WorkoutRepo(this)
        eventCentre = EventCentre
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onHandleWork(intent: Intent) {
        logger.info("onHandleWork")

        launch {
            val syncSource = syncSourceRepository.getDefaultSyncSource()
            if (syncSource != null) {
                logger.debug("onHandleWork | syncType=%s", syncSource.type)

                val goals = getGoalsForUpdate(intent.getLongExtra(EXTRA_GOAL_ID, -1L))

                goals.forEach {
                    logger.debug("onHandleWork | syncGoalId=%s", it.id)
                    eventCentre.publish(WorkoutSyncEvent(it.id, true))

                    val workoutsFromSource = getWorkoutsFromSource(it, syncSource)
                    persistWorkouts(it, workoutsFromSource)

                    // GET LATEST AND GREATEST FROM REPO
                    val workouts = workoutRepository.getAllForGoal(it.id)
                    val distanceInMetre = totalDistanceForWorkouts(workouts)
                    if (distanceInMetre > -1f) {
                        updateGoalWithNewDistance(it, Distance.fromMetres(distanceInMetre), syncSource)

                        //NOTIFY workouts FINISHED UPDATING
                        eventCentre.publish(WorkoutSyncEvent(it.id, false))
                    }

                    logger.debug("onHandleWork | distance=$distanceInMetre")
                }
            } else {
                logger.error("onHandleWork | no Default Sync Source found")
                eventCentre.publish(NoDefaultSyncSource())
            }
        }
    }

    private suspend fun getWorkoutsFromSource(goal: RunningGoal, syncSource: SyncSource): List<Workout> {
        logger.info("getWorkoutsFromSource")

        //TODO: RESOLVE DYNAMICALLY BASED ON CONFIG
        val syncSourceProvider: SyncSourceProvider = StravaSyncSourceProvider()

        syncSourceProvider.setSyncSourceProfile(syncSource)
        return syncSourceProvider.getWorkoutsForDateRange(
                goal.target.period.from.asEpochUtc(),
                goal.target.period.to.asEpochUtc())
    }

    // SUPPORT LOGIC
    private suspend fun getGoalsForUpdate(singleGoalId: Long): List<RunningGoal> {
        val goals: List<RunningGoal> = if (singleGoalId > 0) {
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
        logger.info("updateGoalWithNewDistance")
        logger.debug("updateGoalWithNewDistance | newDistance=%s".format(distance.value))
        runningGoal.progress.distanceToday = distance
        goalRepository.save(runningGoal)

        syncSourceRepository.save(syncSource)
    }

    private suspend fun persistWorkouts(goal: RunningGoal, workouts: List<Workout>) {
        workoutRepository.deleteAllForGoal(goal.id)
        workoutRepository.insertAll(goal.id, workouts)
    }

    private fun totalDistanceForWorkouts(workouts: List<Workout>): Long {
        logger.debug("totalDistanceForWorkouts")
        val distanceMetre: Long = workouts.asSequence().map { it.distanceInMetres }.sum()
        logger.debug("totalDistanceForWorkouts | distanceMetre=%s".format(distanceMetre))
        return distanceMetre
    }
}