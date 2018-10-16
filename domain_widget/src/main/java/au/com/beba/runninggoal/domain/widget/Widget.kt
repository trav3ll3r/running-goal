package au.com.beba.runninggoal.domain.widget


data class Widget(
        val id: Int = 0,
        val goalId: Long = 0L,
        var view: WidgetView = WidgetView()
)