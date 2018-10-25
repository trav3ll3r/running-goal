package au.com.beba.runninggoal.feature.goal

import au.com.beba.runninggoal.domain.RunningGoal


interface GoalActionListener {
    fun createGoal()
    fun editGoal(runningGoal: RunningGoal)
}