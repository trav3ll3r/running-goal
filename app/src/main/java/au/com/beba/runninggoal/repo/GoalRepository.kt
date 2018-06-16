package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.models.RunningGoal

interface GoalRepository {
    suspend fun getGoalForWidget(appWidgetId: Int): RunningGoal

    suspend fun save(goal: RunningGoal, appWidgetId: Int)
}