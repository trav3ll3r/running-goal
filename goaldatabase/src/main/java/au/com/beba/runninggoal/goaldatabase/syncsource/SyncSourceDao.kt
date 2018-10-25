package au.com.beba.runninggoal.goaldatabase.syncsource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface SyncSourceDao {

    @Query("SELECT * FROM sync_source")
    fun getAll(): List<SyncSourceEntity>

    @Query("SELECT * FROM sync_source WHERE type = :type")
    fun getForType(type: String): List<SyncSourceEntity>

    @Query("SELECT * FROM sync_source WHERE is_active = 1")
    fun getDefault(): SyncSourceEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(syncSource: SyncSourceEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(syncSource: SyncSourceEntity)

    @Delete
    fun delete(syncSource: SyncSourceEntity)
}