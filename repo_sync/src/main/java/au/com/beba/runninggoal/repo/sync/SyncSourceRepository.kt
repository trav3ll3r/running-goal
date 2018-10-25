package au.com.beba.runninggoal.repo.sync

import au.com.beba.runninggoal.domain.workout.sync.SyncSource


interface SyncSourceRepository {

    suspend fun getSyncSources(): List<SyncSource>

    suspend fun getSyncSourceForType(type: String): List<SyncSource>

    suspend fun getDefaultSyncSource(): SyncSource?

    suspend fun save(syncSource: SyncSource)
}