package au.com.beba.runninggoal.goaldatabase

import au.com.beba.runninggoal.domain.widget.Widget

interface WidgetStorage {

    suspend fun getId(widgetId: Int): Widget?

    suspend fun allForGoal(goalId: Long): List<Widget>

    suspend fun pairWithGoal(goalId: Long, appWidgetId: Int)

    suspend fun save(widget: Widget)

    suspend fun delete(widgetId: Int): Int
}