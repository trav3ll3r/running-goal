package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update


@Dao
interface SyncSourceDao {

    @Query("SELECT * FROM sync_source")
    fun getAll(): List<SyncSourceEntity>

    @Query("SELECT * FROM sync_source WHERE type = :type")
    fun getForType(type: String): SyncSourceEntity?

    @Query("SELECT * FROM sync_source WHERE is_active = 1")
    fun getDefault(): SyncSourceEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(syncSource: SyncSourceEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(syncSource: SyncSourceEntity)

    @Delete
    fun delete(syncSource: SyncSourceEntity)
}