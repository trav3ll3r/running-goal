package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.models.Widget


interface WidgetRepository {

    suspend fun getByWidgetId(widgetId: Int): Widget?

    suspend fun getAllForGoal(goalId: Long): List<Widget>

    suspend fun save(goalId: Long, appWidgetId: Int)

    suspend fun delete(widget: Widget): Int
}