package au.com.beba.runninggoal.repo.workout

import android.content.Context
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.goaldatabase.WorkoutStorage
import au.com.beba.runninggoal.goaldatabase.workout.WorkoutStorageImpl
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext


class WorkoutRepo constructor(
        private val context: Context,
        private val coroutineContext: CoroutineContext = DefaultDispatcher)
    : WorkoutRepository {

    companion object {
        private val TAG = WorkoutRepo::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val workoutStorage: WorkoutStorage by lazy {
        WorkoutStorageImpl(context)
    }

    override suspend fun insertAll(goalId: Long, workouts: List<Workout>) = withContext(coroutineContext) {
        logger.info("insertAll")
        workoutStorage.insertAll(goalId, workouts)
    }

    override suspend fun getAllForGoal(goalId: Long): List<Workout> = withContext(coroutineContext) {
        logger.info("getAllForGoal")
        workoutStorage.allForGoal(goalId)
    }

    override suspend fun deleteAllForGoal(goalId: Long): Int = withContext(coroutineContext) {
        logger.info("deleteAllForGoal")
        workoutStorage.deleteAllForGoal(goalId)
    }
}