package au.com.beba.runninggoal.domain.widget

data class Widget(
        val id: Int = 0,
        val goalId: Long = 0L,
        var view: WidgetView = WidgetView()
)

data class WidgetView(
        var viewType: WidgetViewType = WidgetViewType.PROGRESS_BAR,
        var updating: Boolean = false) {
    fun toggle() {
        viewType = if (viewType == WidgetViewType.PROGRESS_BAR) {
            WidgetViewType.NUMBERS
        } else {
            WidgetViewType.PROGRESS_BAR
        }
    }
}

enum class WidgetViewType(private val dbValue: Int) {
    PROGRESS_BAR(0),
    NUMBERS(1);

    companion object {
        fun fromDbValue(value: Int): WidgetViewType {
            return WidgetViewType.values().find { it.dbValue == value }!!
        }
    }

    fun asDbValue(): Int {
        return dbValue
    }
}
