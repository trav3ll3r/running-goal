package au.com.beba.runninggoal.repo

import android.content.Context
import android.util.Log
import au.com.beba.runninggoal.models.*
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

    override suspend fun getGoalForWidget(appWidgetId: Int): RunningGoal = withContext(coroutineContext) {
        val goalEntity = runningGoalDao.getById(appWidgetId)

        val goal: RunningGoal
        if (goalEntity != null) {
            goal = RunningGoal(
                    goalEntity.uid,
                    goalEntity.goalName,
                    GoalTarget(
                            goalEntity.targetDistance,
                            LocalDate.ofEpochDay(goalEntity.startDate),
                            LocalDate.ofEpochDay(goalEntity.endDate)
                    ),
                    view = GoalView(GoalViewType.fromDbValue(goalEntity.viewType))
            )
            goal.progress.distanceToday = goalEntity.currentDistance
        } else {
            Log.e(TAG, "Goal for appWidgetId=%s not found!!!".format(appWidgetId))
            goal = RunningGoal(0, "", GoalTarget(0, LocalDate.now(), LocalDate.now()))
        }

        setProgress(goal)

        goal
    }

    private fun setProgress(runningGoal: RunningGoal) {
        val today = LocalDate.now()
        val currentDistance: Double = runningGoal.progress.distanceToday

        Log.d(TAG, "startDate=%s endDate=%s".format(runningGoal.target.start, runningGoal.target.end))
        val daysTotal = getTotalDaysBetween(runningGoal.target.start, runningGoal.target.end)
        val daysLapsed = getTotalDaysBetween(runningGoal.target.start, today)
        Log.d(TAG, "daysTotal=%s daysLapsed=%s".format(daysTotal, daysLapsed))

        val linearDistancePerDay = (runningGoal.target.distance * 1.0 / daysTotal)
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(currentDistance, daysTotal, daysLapsed, expectedDistance)
        runningGoal.progress.positionInDistance = currentDistance - expectedDistance
        runningGoal.progress.positionInDays = runningGoal.progress.positionInDistance / linearDistancePerDay

        runningGoal.projection = GoalProjection(linearDistancePerDay, daysLapsed)
    }

    override suspend fun save(goal: RunningGoal, appWidgetId: Int) = withContext(coroutineContext) {
        val goalEntity = RunningGoalEntity(appWidgetId)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance
        goalEntity.currentDistance = goal.progress.distanceToday
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
        return Duration.between(from.atTime(0, 0), to.atTime(0, 0)).toDays().toInt() + 1
    }
}