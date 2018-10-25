package au.com.beba.runninggoal.feature.workout

import android.content.Context
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.repo.workout.WorkoutRepo
import au.com.beba.runninggoal.repo.workout.WorkoutRepository
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object WorkoutFeatureImpl : WorkoutFeature {

    private val TAG = WorkoutFeatureImpl::class.java.simpleName

    private lateinit var workoutRepo: WorkoutRepository
    private val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun bootstrap(application: Context) {
        workoutRepo = WorkoutRepo(application)
    }

    override var isSuspended = false
    override var isReady = !isSuspended

    override fun getAllForGoal(goalId: Long): List<Workout>? {
        logger.info("getAllForGoal")
        return runBlocking { workoutRepo.getAllForGoal(goalId) }
    }

    override fun deleteAllForGoal(goalId: Long) {
        logger.info("deleteAllForGoal")
        runBlocking { workoutRepo.deleteAllForGoal(goalId) }
    }
}