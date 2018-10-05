package au.com.beba.runninggoal.domain.sync

import java.time.LocalDateTime

data class SyncSource(
        val id: Int = 0,
        var type: String = "",
        var accessToken: String = "",
        var isDefault: Boolean = false,
        var syncedAt: LocalDateTime = LocalDateTime.now()
)