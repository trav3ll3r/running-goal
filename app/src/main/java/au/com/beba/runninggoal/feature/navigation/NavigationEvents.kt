package au.com.beba.runninggoal.feature.navigation

import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.Event
import au.com.beba.runninggoal.feature.goals.GoalViewHolder


/* *********** */
/* GOAL EVENTS */
/* *********** */
class ShowGoalsEvent : Event
class ShowEditGoalEvent(val goalId: Long?) : Event
class ShowGoalDetailsEvent(val runningGoal: RunningGoal, val viewHolder: GoalViewHolder) : Event


/* ****************** */
/* SYNC SOURCE EVENTS */
/* ****************** */
class ShowSyncSourcesEvent : Event
class ShowEditSyncSourceEvent(val syncSourceId: Long?) : Event
