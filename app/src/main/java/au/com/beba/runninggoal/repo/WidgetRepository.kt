package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.domain.widget.Widget


interface WidgetRepository {

    suspend fun getByWidgetId(widgetId: Int): Widget?

    suspend fun getAllForGoal(goalId: Long): List<Widget>

    suspend fun pairWithGoal(goalId: Long, appWidgetId: Int)

    suspend fun save(widget: Widget)

    suspend fun delete(widget: Widget): Int
}