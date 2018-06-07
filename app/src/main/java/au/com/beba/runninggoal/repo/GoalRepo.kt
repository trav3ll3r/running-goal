package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.models.GoalProgress
import au.com.beba.runninggoal.models.GoalProjection
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.RunningGoal
import java.time.LocalDate

object GoalRepo {
    fun getGoalById(id: Int): RunningGoal {

        val goal = when (id) {
            1 -> RunningGoal("1", "Jun 2018", GoalTarget(200, LocalDate.of(2018, 5, 1), LocalDate.of(2018, 5, 30)))
            else -> RunningGoal("2", "May 2018", GoalTarget(100, LocalDate.of(2018, 4, 1), LocalDate.of(2018, 4, 31)))
        }

        setProgress(goal)

        return goal
    }

    private fun setProgress(runningGoal: RunningGoal) {
        val today = LocalDate.now()

        val daysTotal = java.time.Period.between(runningGoal.target.start, runningGoal.target.end).days
        val daysLapsed = java.time.Period.between(runningGoal.target.start, today).days

        val linearDistancePerDay = (runningGoal.target.distance / daysTotal) * 1.0
        val expectedDistance = linearDistancePerDay * daysLapsed

        runningGoal.progress = GoalProgress(45, daysLapsed, expectedDistance)

        runningGoal.projection = GoalProjection(linearDistancePerDay, daysLapsed)
    }
}