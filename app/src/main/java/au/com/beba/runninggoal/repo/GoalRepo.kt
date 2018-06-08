package au.com.beba.runninggoal.repo

import android.content.Context
import au.com.beba.runninggoal.models.GoalProgress
import au.com.beba.runninggoal.models.GoalProjection
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.RunningGoal
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
                    )
            )
            setProgress(goal, goalEntity.currentDistance)
        }


        return goal
    }

    private fun setProgress(runningGoal: RunningGoal, currentDistance: Double) {
        val today = LocalDate.now()

        val daysTotal = java.time.Period.between(runningGoal.target.start, runningGoal.target.end).days
        val daysLapsed = java.time.Period.between(runningGoal.target.start, today).days

        val linearDistancePerDay = (runningGoal.target.distance / daysTotal) * 1.0
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(currentDistance, daysLapsed, expectedDistance)

        runningGoal.projection = GoalProjection(linearDistancePerDay, daysLapsed)
    }

    fun save(goal: RunningGoal, widgetId: Int) {
        val goalEntity = RunningGoalEntity(widgetId)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance
        goalEntity.startDate = goal.target.start.toEpochDay()
        goalEntity.endDate = goal.target.end.toEpochDay()
        goalEntity.widgetId = widgetId
        db?.runningGoalDao()?.insertAll(goalEntity)
    }
}