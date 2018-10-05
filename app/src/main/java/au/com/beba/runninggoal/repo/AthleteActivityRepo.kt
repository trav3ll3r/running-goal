package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.domain.core.Workout
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.AthleteActivityDao
import au.com.beba.runninggoal.persistence.AthleteActivityEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.CoroutineContext


class AthleteActivityRepo private constructor(
        private val coroutineContext: CoroutineContext,
        private val athleteActivityDao: AthleteActivityDao) : AthleteActivityRepository {

    companion object {
        private val TAG = AthleteActivityRepo::class.java.simpleName
        private lateinit var db: AppDatabase
        private var INSTANCE: AthleteActivityRepository? = null

        @JvmStatic
        fun getInstance(context: Context, coroutineContext: CoroutineContext = DefaultDispatcher): AthleteActivityRepository {
            if (INSTANCE == null) {
                synchronized(AthleteActivityRepo::javaClass) {
                    db = AppDatabase.getInstance(context)
                    INSTANCE = AthleteActivityRepo(coroutineContext, db.athleteActivityDao())
                }
            }
            return INSTANCE!!
        }
    }

    override suspend fun insertAll(goalId: Long, workouts: List<Workout>) {
        athleteActivityDao.insert(workouts.map {
            AthleteActivityEntity(
                    goalId,
                    it.name ?: "",
                    it.description ?: "",
                    it.distanceInMetres,
                    it.dateTime
            )
        })

    }

    override suspend fun getAllForGoal(goalId: Long): List<Workout> = withContext(coroutineContext) {
        Log.i(TAG, "getAllForGoal")
        Log.v(TAG, "getAllForGoal | goalId=%s".format(goalId))
        val entities = athleteActivityDao.getAllForGoal(goalId)
        val workouts: List<Workout> = entities.asSequence().map { entity2model(it)!! }.toList()
        Log.d(TAG, "athleteActivities=%s".format(workouts.size))
        workouts
    }

    override suspend fun deleteAllForGoal(goalId: Long): Int = withContext(coroutineContext) {
        Log.i(TAG, "deleteAllForGoal")
        val deletedRows = athleteActivityDao.deleteAllForGoal(goalId)
        Log.d(TAG, "deleteAllForGoal | athleteActivities=%s".format(deletedRows))
        deletedRows
    }

    private fun entity2model(entity: AthleteActivityEntity?): Workout? {
        return if (entity != null) {
            Workout(
                    entity.name,
                    entity.description,
                    entity.distanceInMetres,
                    entity.dateTime,
                    entity.uid
            )
        } else {
            null
        }
    }
}