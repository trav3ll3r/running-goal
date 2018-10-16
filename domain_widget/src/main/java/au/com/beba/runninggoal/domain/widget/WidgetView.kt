package au.com.beba.runninggoal.domain.widget


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