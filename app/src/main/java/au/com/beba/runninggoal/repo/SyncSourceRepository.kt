package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.models.SyncSource


interface SyncSourceRepository {
    suspend fun getSyncSources(): List<SyncSource>

    suspend fun getSyncSourceForType(type: String): SyncSource

    suspend fun getDefaultSyncSource(): SyncSource

    suspend fun save(syncSource: SyncSource)
}