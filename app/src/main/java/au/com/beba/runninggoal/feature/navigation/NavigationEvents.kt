package au.com.beba.runninggoal.feature.navigation

import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.Event
import au.com.beba.runninggoal.feature.goals.GoalViewHolder


class ShowGoalsEvent : Event
class ShowEditGoalEvent(val goalId: Long?) : Event
class ShowGoalDetailsEvent(val runningGoal: RunningGoal, val viewHolder: GoalViewHolder) : Event
class ShowSyncSourcesEvent : Event
class ShowEditSyncSourceEvent(val syncSourceId: Long?) : Event