package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.domain.Workout
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.WorkoutDao
import au.com.beba.runninggoal.persistence.WorkoutEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.CoroutineContext


class WorkoutRepo private constructor(
        private val coroutineContext: CoroutineContext,
        private val workoutDao: WorkoutDao) : WorkoutRepository {

    companion object {
        private val TAG = WorkoutRepo::class.java.simpleName
        private lateinit var db: AppDatabase
        private var INSTANCE: WorkoutRepository? = null

        @JvmStatic
        fun getInstance(context: Context, coroutineContext: CoroutineContext = DefaultDispatcher): WorkoutRepository {
            if (INSTANCE == null) {
                synchronized(WorkoutRepo::javaClass) {
                    db = AppDatabase.getInstance(context)
                    INSTANCE = WorkoutRepo(coroutineContext, db.workoutDao())
                }
            }
            return INSTANCE!!
        }
    }

    override suspend fun insertAll(goalId: Long, workouts: List<Workout>) = withContext(coroutineContext) {
        Log.i(TAG, "insertAll")
        Log.v(TAG, "insertAll | workouts=%s".format(workouts.size))

        val rows = workoutDao.insert(workouts.map {
            WorkoutEntity(
                    goalId,
                    it.name ?: "",
                    it.description ?: "",
                    it.distanceInMetres,
                    it.dateTime
            )
        })

        Log.i(TAG, "insertAll | rows=%s".format(rows.size))
        Unit
    }

    override suspend fun getAllForGoal(goalId: Long): List<Workout> = withContext(coroutineContext) {
        Log.i(TAG, "getAllForGoal")
        Log.v(TAG, "getAllForGoal | goalId=%s".format(goalId))
        val entities = workoutDao.getAllForGoal(goalId)
        val workouts: List<Workout> = entities.asSequence().map { entity2model(it)!! }.toList()
        Log.d(TAG, "getAllForGoal | workouts=%s".format(workouts.size))
        workouts
    }

    override suspend fun deleteAllForGoal(goalId: Long): Int = withContext(coroutineContext) {
        Log.i(TAG, "deleteAllForGoal")
        val deletedRows = workoutDao.deleteAllForGoal(goalId)
        Log.d(TAG, "deleteAllForGoal | workouts=%s".format(deletedRows))
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