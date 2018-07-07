package au.com.beba.runninggoal.repo

import android.arch.lifecycle.LiveData
import au.com.beba.runninggoal.models.RunningGoal

interface GoalRepository {
    fun getGoals(): LiveData<List<RunningGoal>>

    suspend fun fetchGoals(): List<RunningGoal>

    suspend fun getGoalForWidget(appWidgetId: Int): RunningGoal

    suspend fun save(goal: RunningGoal, appWidgetId: Int)

    suspend fun delete(goal: RunningGoal): Int
}