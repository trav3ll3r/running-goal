package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.GoalDate
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.Period
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.RunningGoalDao
import au.com.beba.runninggoal.persistence.RunningGoalEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.CoroutineContext


class GoalRepo private constructor(
        private val coroutineContext: CoroutineContext,
        private val runningGoalDao: RunningGoalDao) : GoalRepository {

    companion object {
        private val TAG = GoalRepo::class.java.simpleName

        private lateinit var db: AppDatabase
        private var INSTANCE: GoalRepository? = null

        @JvmStatic
        fun getInstance(context: Context, coroutineContext: CoroutineContext = DefaultDispatcher): GoalRepository {
            if (INSTANCE == null) {
                synchronized(GoalRepo::javaClass) {
                    db = AppDatabase.getInstance(context)
                    INSTANCE = GoalRepo(coroutineContext, db.runningGoalDao())
                }
            }
            return INSTANCE!!
        }
    }

    private val cachedGoals = MutableLiveData<List<RunningGoal>>()

    override fun getGoals(): LiveData<List<RunningGoal>> {
        return cachedGoals
    }

    override suspend fun fetchGoals(): List<RunningGoal> = withContext(coroutineContext) {
        val goalEntities = runningGoalDao.getAll()

        val goals = goalEntities.asSequence().map {
            val goal = RunningGoal(
                    it.uid,
                    it.goalName,
                    GoalTarget(
                            Distance(it.targetDistance),
                            buildUtcPeriod(it)
                    )
            )
            goal.progress.distanceToday = Distance(it.currentDistance)
            goal.updateProgressValues()

            goal
        }.toList()

        Log.d(TAG, "goals=%s".format(goals.size))

        cachedGoals.postValue(goals)
        goals
    }

    override suspend fun getById(goalId: Long): RunningGoal? = withContext(coroutineContext) {
        val goalEntity = runningGoalDao.getById(goalId)

        val goal: RunningGoal?
        if (goalEntity != null) {
            goal = RunningGoal(
                    goalEntity.uid,
                    goalEntity.goalName,
                    GoalTarget(
                            Distance(goalEntity.targetDistance),
                            buildUtcPeriod(goalEntity)
                    )
            )

            goal.progress.distanceToday = Distance(goalEntity.currentDistance)
            goal.updateProgressValues()
        } else {
            Log.e(TAG, "Goal for goalId=%s not found!".format(goalId))
            goal = null
        }

        goal
    }

    override suspend fun markGoalUpdateStatus(updating: Boolean, runningGoal: RunningGoal) = withContext(coroutineContext) {
        runningGoal.view.updating = updating
        cachedGoals.postValue(placeGoalInLiveData(runningGoal))
    }

    override suspend fun save(goal: RunningGoal): Long = withContext(coroutineContext) {
        Log.i(TAG, "save")
        Log.d(TAG, "save | mapFrom | id=%s, distance=%s".format(goal.id, goal.progress.distanceToday.value))

        val goalEntity = RunningGoalEntity(goal.id)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance.value
        goalEntity.currentDistance = goal.progress.distanceToday.value

        goalEntity.startDate = goal.target.period.from.asEpochUtc()
        goalEntity.endDate = goal.target.period.to.asEpochUtc()

        var id: Long = runningGoalDao.insert(goalEntity)
        if (id < 0L) {
            Log.i(TAG, "save | update")
            Log.d(TAG, "save | update | uid=%s, distance=%s".format(goalEntity.uid, goalEntity.currentDistance))
            runningGoalDao.update(goalEntity)
            id = goalEntity.uid
        }

        goal.updateProgressValues()

        cachedGoals.postValue(placeGoalInLiveData(goal))
        id
    }

    override suspend fun delete(goal: RunningGoal): Int = withContext(coroutineContext) {
        val goalEntity = RunningGoalEntity(goal.id)
        val deletedRows = runningGoalDao.delete(goalEntity)

        // TODO: UPDATE cachedGoals LIVE DATA
        //cachedGoals.postValue(removeGoalFromLiveData(goal))

        deletedRows
    }

    private fun buildUtcPeriod(goalEntity: RunningGoalEntity): Period {
        return Period(GoalDate(goalEntity.startDate), GoalDate(goalEntity.endDate))
    }

    /**
     * Puts [goal] in the list.
     *
     * If [goal] id exists, [goal] will replace it
     *
     * If [goal] id does not exist, [goal] will be appended to the list
     */
    private fun placeGoalInLiveData(goal: RunningGoal): List<RunningGoal> {
        val currentGoals = cachedGoals.value?.toMutableList() ?: mutableListOf()
        val index = currentGoals.indexOfFirst { it.id == goal.id }
        if (index > -1) {
            currentGoals[index] = goal
        } else {
            currentGoals.add(goal)
        }
        return currentGoals
    }
}