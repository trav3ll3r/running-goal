package au.com.beba.runninggoal.feature.appevents

import au.com.beba.runninggoal.domain.event.Event


/* *********** */
/* GOAL EVENTS */
/* *********** */
class GoalChangeEvent(val goalId: Long) : Event
class GoalDeleteEvent(val goalId: Long) : Event

/* ****************** */
/* SYNC SOURCE EVENTS */
/* ****************** */
class SyncSourceChange : Event
class SyncSourceDelete : Event
class NoDefaultSyncSource : Event

/* ************** */
/* WORKOUT EVENTS */
/* ************** */
class WorkoutSyncEvent(val goalId: Long, val isUpdating: Boolean) : Event

/* ************* */
/* WIDGET EVENTS */
/* ************* */
class WidgetChangeEvent(val widgetId: Long, val goalId: Long?) : Event
class WidgetDeleteEvent(val widgetId: Long) : Event