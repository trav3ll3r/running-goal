package au.com.beba.runninggoal.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.GoalView
import au.com.beba.runninggoal.models.GoalViewType
import au.com.beba.runninggoal.models.Period
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.RunningGoalDao
import au.com.beba.runninggoal.persistence.RunningGoalEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import java.time.LocalDate
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

        val goals = goalEntities.map {
            val goal = RunningGoal(
                    it.uid,
                    it.goalName,
                    GoalTarget(
                            Distance(it.targetDistance),
                            Period(LocalDate.ofEpochDay(it.startDate), LocalDate.ofEpochDay(it.endDate))
                    ),
                    view = GoalView(GoalViewType.fromDbValue(it.viewType))
            )
            goal.progress.distanceToday = Distance(it.currentDistance)
            goal.updateProgressValues()

            goal
        }.toList()

        Log.d(TAG, "goals=%s".format(goals.size))

        cachedGoals.postValue(goals)
        goals
    }

    override suspend fun getGoalForWidget(appWidgetId: Int): RunningGoal = withContext(coroutineContext) {
        val goalEntity = runningGoalDao.getById(appWidgetId)

        val goal: RunningGoal
        if (goalEntity != null) {
            goal = RunningGoal(
                    goalEntity.uid,
                    goalEntity.goalName,
                    GoalTarget(
                            Distance(goalEntity.targetDistance),
                            Period(LocalDate.ofEpochDay(goalEntity.startDate), LocalDate.ofEpochDay(goalEntity.endDate))
                    ),
                    view = GoalView(GoalViewType.fromDbValue(goalEntity.viewType))
            )
            goal.progress.distanceToday = Distance(goalEntity.currentDistance)
        } else {
            Log.e(TAG, "Goal for appWidgetId=%s not found!!!".format(appWidgetId))
            goal = RunningGoal()
        }
        goal.updateProgressValues()

        goal
    }

    override suspend fun markGoalUpdateStatus(updating: Boolean, runningGoal: RunningGoal) = withContext(coroutineContext) {
        runningGoal.view.updating = updating
        cachedGoals.postValue(placeGoalInLiveData(runningGoal))
    }

    override suspend fun save(goal: RunningGoal, appWidgetId: Int) = withContext(coroutineContext) {
        Log.d(TAG, "save")
        Log.d(TAG, "mapFrom | id=%s, distance=%s".format(goal.id, goal.progress.distanceToday.value))

        val goalEntity = RunningGoalEntity(appWidgetId)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance.value
        goalEntity.currentDistance = goal.progress.distanceToday.value
        goalEntity.startDate = goal.target.period.from.toEpochDay()
        goalEntity.endDate = goal.target.period.to.toEpochDay()
        goalEntity.widgetId = appWidgetId
        goalEntity.viewType = goal.view.viewType.asDbValue()

        val id: Long = runningGoalDao.insert(goalEntity)
        if (id < 0L) {
            Log.d(TAG, "update")
            Log.d(TAG, "update | uid=%s, distance=%s".format(goalEntity.uid, goalEntity.currentDistance))
            runningGoalDao.update(goalEntity)
        }
        goal.updateProgressValues()
        cachedGoals.postValue(placeGoalInLiveData(goal))
    }

    override suspend fun delete(goal: RunningGoal): Int = withContext(coroutineContext) {
        val goalEntity = RunningGoalEntity(goal.id)
        val deletedRows = runningGoalDao.delete(goalEntity)

        // TODO: UPDATE cachedGoals LIVE DATA
        //cachedGoals.postValue(removeGoalFromLiveData(goal))

        deletedRows
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