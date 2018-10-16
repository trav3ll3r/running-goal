package au.com.beba.runninggoal.goaldatabase.workout

import android.content.Context
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.goaldatabase.AppDatabase
import au.com.beba.runninggoal.goaldatabase.WorkoutStorage
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.coroutineContext

class WorkoutStorageImpl(private val context: Context)
    : WorkoutStorage {

    companion object {
        private val TAG = WorkoutStorageImpl::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val workoutDao: WorkoutDao by lazy {
        AppDatabase.getInstance(context).workoutDao()
    }

    override suspend fun insertAll(goalId: Long, workouts: List<Workout>) = withContext(coroutineContext) {
        logger.info("insertAll")
        logger.debug("insertAll | workouts=%s".format(workouts.size))

        val rows = workoutDao.insert(workouts.map {
            WorkoutEntity(
                    goalId,
                    it.name ?: "",
                    it.description ?: "",
                    it.distanceInMetres,
                    it.dateTime
            )
        })

        logger.info("insertAll | rows=%s".format(rows.size))
        Unit
    }

    override suspend fun allForGoal(goalId: Long): List<Workout> = withContext(coroutineContext) {
        logger.info("allForGoal")
        logger.debug("allForGoal | goalId=%s".format(goalId))

        val entities = workoutDao.getAllForGoal(goalId)
        val workouts: List<Workout> = entities.asSequence().map { entity2model(it)!! }.toList()
        logger.debug("getAllForGoal | workouts=%s".format(workouts.size))
        workouts
    }

    override suspend fun deleteAllForGoal(goalId: Long): Int = withContext(coroutineContext) {
        logger.info("deleteAllForGoal")
        val deletedRows = workoutDao.deleteAllForGoal(goalId)
        logger.debug("deleteAllForGoal | workouts=%s".format(deletedRows))
        deletedRows
    }

    private fun entity2model(entity: WorkoutEntity?): Workout? {
        return if (entity != null) {
            Workout(
                    entity.name,
                    entity.description,
                    entity.distanceInMetres.toFloat(),
                    entity.dateTime,
                    entity.uid
            )
        } else {
            null
        }
    }
}