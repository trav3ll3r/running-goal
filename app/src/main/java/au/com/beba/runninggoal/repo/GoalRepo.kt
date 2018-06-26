package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.GoalProgress
import au.com.beba.runninggoal.models.GoalProjection
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.GoalView
import au.com.beba.runninggoal.models.GoalViewType
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.RunningGoalDao
import au.com.beba.runninggoal.persistence.RunningGoalEntity
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import java.time.Duration
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

    override suspend fun getGoals(): List<RunningGoal> = withContext(coroutineContext) {
        val goalEntities = runningGoalDao.getAll()

        val goals = goalEntities.map {
            val goal = RunningGoal(
                    it.uid,
                    it.goalName,
                    GoalTarget(
                            Distance(it.targetDistance),
                            LocalDate.ofEpochDay(it.startDate),
                            LocalDate.ofEpochDay(it.endDate)
                    ),
                    view = GoalView(GoalViewType.fromDbValue(it.viewType))
            )
            goal.progress.distanceToday = Distance(it.currentDistance)

            setProgress(goal, LocalDate.now())
            goal
        }.toList()

        Log.d(TAG, "goals=%s".format(goals.size))

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
                            LocalDate.ofEpochDay(goalEntity.startDate),
                            LocalDate.ofEpochDay(goalEntity.endDate)
                    ),
                    view = GoalView(GoalViewType.fromDbValue(goalEntity.viewType))
            )
            goal.progress.distanceToday = Distance(goalEntity.currentDistance)
        } else {
            Log.e(TAG, "Goal for appWidgetId=%s not found!!!".format(appWidgetId))
            goal = RunningGoal()
        }

        setProgress(goal, LocalDate.now())

        goal
    }

    private fun setProgress(runningGoal: RunningGoal, today: LocalDate) {
        val currentDistance = runningGoal.progress.distanceToday.value
        val endLapsedDate = getLapsedEndDate(runningGoal, today)

        val daysTotal = getTotalDaysBetween(runningGoal.target.start, runningGoal.target.end)
        Log.d(TAG, "startDate=%s endDate=%s".format(runningGoal.target.start, endLapsedDate))
        val daysLapsed = getTotalDaysBetween(runningGoal.target.start, endLapsedDate)
        Log.d(TAG, "daysTotal=%s daysLapsed=%s".format(daysTotal, daysLapsed))

        val linearDistancePerDay = (runningGoal.target.distance.value / daysTotal)
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(Distance(currentDistance), daysTotal, daysLapsed, Distance(expectedDistance))
        runningGoal.progress.positionInDistance = Distance(currentDistance - expectedDistance)
        runningGoal.progress.positionInDays = runningGoal.progress.positionInDistance.value / linearDistancePerDay

        runningGoal.projection = GoalProjection(Distance(linearDistancePerDay), daysLapsed)
    }

    private fun getLapsedEndDate(runningGoal: RunningGoal, today: LocalDate): LocalDate {
        return if (runningGoal.target.end.isAfter(today)) today else runningGoal.target.end
    }

    override suspend fun save(goal: RunningGoal, appWidgetId: Int) = withContext(coroutineContext) {
        val goalEntity = RunningGoalEntity(appWidgetId)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance.value
        goalEntity.currentDistance = goal.progress.distanceToday.value
        goalEntity.startDate = goal.target.start.toEpochDay()
        goalEntity.endDate = goal.target.end.toEpochDay()
        goalEntity.widgetId = appWidgetId
        goalEntity.viewType = goal.view.viewType.asDbValue()

        val id: Long = runningGoalDao.insert(goalEntity)
        if (id < 0L) {
            runningGoalDao.update(goalEntity)
        }
    }

    private fun getTotalDaysBetween(from: LocalDate, to: LocalDate): Int {
        if (from.isBefore(to)) {
            return Duration.between(from.atTime(0, 0), to.atTime(0, 0)).toDays().toInt() + 1
        }
        return 0
    }
}