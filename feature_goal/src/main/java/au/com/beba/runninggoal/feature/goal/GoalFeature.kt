package au.com.beba.runninggoal.feature.goal

import au.com.beba.feature.base.AndroidFeature
import au.com.beba.runninggoal.domain.RunningGoal

interface GoalFeature : AndroidFeature {

    fun getById(goalId: Long): RunningGoal?

    fun fetchGoals(): List<RunningGoal>

    fun save(goal: RunningGoal): Long

    fun delete(goal: RunningGoal): Int
}