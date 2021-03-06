package au.com.beba.runninggoal.domain.workout.sync

import java.time.LocalDateTime

data class SyncSource(
        val id: Long = 0,
        var nickname: String = "",
        var type: String = "",
        var accessToken: String = "",
        var isDefault: Boolean = false,
        var syncedAt: LocalDateTime = LocalDateTime.now()
)