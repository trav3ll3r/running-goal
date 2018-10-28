package au.com.beba.runningoal.repo.widget

import au.com.beba.runninggoal.domain.widget.Widget


interface WidgetRepository {

    suspend fun getById(widgetId: Int): Widget?

    suspend fun getAllForGoal(goalId: Long): List<Widget>

    suspend fun save(widget: Widget)

    suspend fun delete(widgetId: Int): Int

    suspend fun pairWithGoal(goalId: Long, appWidgetId: Int)
}