package au.com.beba.runninggoal.feature.goal.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.NumericWithLabel
import au.com.beba.runninggoal.domain.Distance
import au.com.beba.runninggoal.domain.core.display
import au.com.beba.runninggoal.domain.workout.Workout
import org.jetbrains.anko.find


class GoalWorkoutsSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val layoutId = R.layout.list_item_workouts_summary
    }

    private val label: TextView = itemView.find(R.id.label)
    private val count: NumericWithLabel = itemView.find(R.id.workouts_summary_count)
    private val distance: NumericWithLabel = itemView.find(R.id.workouts_summary_distance)

    fun bindView(workouts: List<Workout>?) {
        if (workouts != null) {
            val context = this.itemView.context
            val totalDistance = Distance(workouts.sumBy { it.distanceInMetres.toInt() }.toFloat() / 1000)
            count.setValues(workouts.size.toString(), context.getString(R.string.workouts_summary_count))
            distance.setValues(totalDistance.display(true), context.getString(R.string.workouts_summary_distance))
        }
    }
}