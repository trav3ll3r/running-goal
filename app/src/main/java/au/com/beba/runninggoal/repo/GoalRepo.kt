package au.com.beba.runninggoal.repo

import android.content.Context
import au.com.beba.runninggoal.models.*
import au.com.beba.runninggoal.persistence.AppDatabase
import au.com.beba.runninggoal.persistence.RunningGoalEntity
import java.time.LocalDate


object GoalRepo {

    private var db: AppDatabase? = null

    fun initialise(context: Context) {
        if (db == null) {
            db = AppDatabase.getInstance(context)
        }
    }

    fun getGoalForWidget(widgetId: Int): RunningGoal? {
        val goalEntity = db?.runningGoalDao()?.getById(widgetId)

        var goal: RunningGoal? = null
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
            setProgress(goal, goalEntity.currentDistance)
        }

        return goal
    }

    private fun setProgress(runningGoal: RunningGoal, currentDistance: Double) {
        val today = LocalDate.now()

        val daysTotal = java.time.Period.between(runningGoal.target.start, runningGoal.target.end).days + 1
        val daysLapsed = java.time.Period.between(runningGoal.target.start, today).days + 1

        val linearDistancePerDay = (runningGoal.target.distance * 1.0 / daysTotal)
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(currentDistance, daysTotal, daysLapsed, expectedDistance)
        runningGoal.progress.positionInDistance = currentDistance - expectedDistance
        runningGoal.progress.positionInDays = runningGoal.progress.positionInDistance / linearDistancePerDay

        runningGoal.projection = GoalProjection(linearDistancePerDay, daysLapsed)
    }

    fun save(goal: RunningGoal, widgetId: Int) {
        val goalEntity = RunningGoalEntity(widgetId)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance
        goalEntity.currentDistance = goal.progress.distanceToday
        goalEntity.startDate = goal.target.start.toEpochDay()
        goalEntity.endDate = goal.target.end.toEpochDay()
        goalEntity.widgetId = widgetId
        goalEntity.viewType = goal.view.viewType.asDbValue()

        val goalDao = db?.runningGoalDao()

        val id : Long = goalDao?.insert(goalEntity) ?: 0
        if (id < 0L) {
            goalDao?.update(goalEntity)
        }
    }
}