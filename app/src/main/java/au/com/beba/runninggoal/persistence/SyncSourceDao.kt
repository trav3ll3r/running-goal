package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.*


@Dao
interface SyncSourceDao {

    @Query("SELECT * FROM sync_source")
    fun getAll(): List<SyncSourceEntity>

    @Query("SELECT * FROM sync_source WHERE type = :type")
    fun getForType(type: String): SyncSourceEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(syncSource: SyncSourceEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(syncSource: SyncSourceEntity)

    @Delete
    fun delete(syncSource: SyncSourceEntity)
}