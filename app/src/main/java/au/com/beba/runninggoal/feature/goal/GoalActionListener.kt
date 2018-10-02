package au.com.beba.runninggoal.feature.goal

import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import au.com.beba.runninggoal.models.RunningGoal

interface GoalActionListener {
    fun createGoal()
    fun editGoal(runningGoal: RunningGoal)
    fun syncGoal(runningGoal: RunningGoal)
    fun showGoalDetails(runningGoal: RunningGoal, holder: GoalViewHolder)
}