package au.com.beba.runninggoal.domain.widget

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
