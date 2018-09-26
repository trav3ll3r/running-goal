package au.com.beba.runninggoal.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


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