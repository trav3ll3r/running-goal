package au.com.beba.runninggoal.feature.navigation

import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.goals.GoalViewHolder

interface AppScreen
class GoalsScreen : AppScreen
class GoalDetailsScreen(val runningGoal: RunningGoal, val holder: GoalViewHolder) : AppScreen
class SyncSourcesScreen : AppScreen