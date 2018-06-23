package au.com.beba.runninggoal.models

import java.time.LocalDate

data class SyncSource(
        val id: Int = 0,
        var type: String = "",
        var accessToken: String = "",
        var syncedAt: LocalDate = LocalDate.now()
)

enum class SyncSourceType(private val dbValue: String) {
    STRAVA("STRAVA")
    //NUMBERS(1)
    ;

    companion object {
        fun fromDbValue(value: String): SyncSourceType {
            return SyncSourceType.values().find { it.dbValue == value }!!
        }
    }

    fun asDbValue(): String {
        return dbValue
    }

}