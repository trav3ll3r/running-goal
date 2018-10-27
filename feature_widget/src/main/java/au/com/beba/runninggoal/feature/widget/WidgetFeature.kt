package au.com.beba.runninggoal.feature.widget

import au.com.beba.feature.base.AndroidFeature
import au.com.beba.runninggoal.domain.widget.Widget


interface WidgetFeature : AndroidFeature {

    fun getById(widgetId: Int): Widget?

    fun getAllForGoal(goalId: Long): List<Widget>

    fun save(widget: Widget)

    fun delete(widgetId: Int)

    fun pairWithGoal(goalId: Long, widgetId: Int)
}
