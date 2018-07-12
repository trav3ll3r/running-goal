package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update


@Dao
interface WidgetDao {

    @Query("SELECT * FROM widget WHERE uid = :widgetId")
    fun getByWidgetId(widgetId: Int): WidgetEntity?

    @Query("SELECT * FROM widget WHERE goal_id = :goalId")
    fun getAllForGoal(goalId: Long): List<WidgetEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(widget: WidgetEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(widget: WidgetEntity)

    @Delete
    fun delete(widget: WidgetEntity): Int
}