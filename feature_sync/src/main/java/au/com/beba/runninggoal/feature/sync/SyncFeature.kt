package au.com.beba.runninggoal.feature.sync

import android.content.Context
import au.com.beba.feature.base.AndroidFeature
import au.com.beba.runninggoal.domain.workout.sync.SourceType
import au.com.beba.runninggoal.domain.workout.sync.SyncSource


interface SyncFeature : AndroidFeature {

    fun getAllConfiguredSources(): List<SyncSource>

    fun getById(syncSourceId: Long): SyncSource?

    fun getSyncSourceTypes(): List<SourceType>

    val defaultSyncSource: SyncSource?

    fun storeSyncSource(syncSource: SyncSource)

    fun syncNow(context: Context, goalId: Long?)
}