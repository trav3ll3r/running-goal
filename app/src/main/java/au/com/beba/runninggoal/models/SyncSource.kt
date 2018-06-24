package au.com.beba.runninggoal.models

import java.time.LocalDateTime

data class SyncSource(
        val id: Int = 0,
        var type: String = "",
        var accessToken: String = "",
        var syncedAt: LocalDateTime = LocalDateTime.now()
)

//enum class SyncSourceType(private val dbValue: String) {
//    STRAVA("STRAVA");
//
//    companion object {
//        fun fromDbValue(value: String): SyncSourceType {
//            return SyncSourceType.values().find { it.dbValue == value }!!
//        }
//    }
//
//    fun asDbValue(): String {
//        return dbValue
//    }
//}