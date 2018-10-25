package au.com.beba.runninggoal.feature.sync

import android.content.Context
import au.com.beba.feature.base.AndroidFeature
import au.com.beba.runninggoal.domain.workout.sync.SyncSource


interface SyncFeature : AndroidFeature {

    fun getSyncSourceForType(syncSourceType: String): SyncSource

    fun getAllConfiguredSources(): List<SyncSource>

    val defaultSyncSource: SyncSource?

    fun storeSyncSource(syncSource: SyncSource)

    fun syncNow(context: Context, goalId: Long?)
}