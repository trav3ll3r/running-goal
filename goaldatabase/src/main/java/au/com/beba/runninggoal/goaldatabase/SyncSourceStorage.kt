package au.com.beba.runninggoal.goaldatabase

import au.com.beba.runninggoal.domain.workout.sync.SyncSource


interface SyncSourceStorage {

    suspend fun all(): List<SyncSource>

    suspend fun allForType(type: String): SyncSource

    suspend fun default(): SyncSource

    suspend fun save(syncSource: SyncSource)
}