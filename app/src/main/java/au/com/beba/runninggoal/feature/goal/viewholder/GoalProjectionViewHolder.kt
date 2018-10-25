package au.com.beba.runninggoal.feature.goal.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.ui.component.display
import au.com.beba.runninggoal.ui.component.NumericWithLabel
import org.jetbrains.anko.find


class GoalProjectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val layoutId = R.layout.list_item_goal_projection
    }

    private val label: TextView = itemView.find(R.id.label)
    private val perDayIdeal: NumericWithLabel = itemView.find(R.id.goal_projection_period_daily)
    private val daysTotal: NumericWithLabel = itemView.find(R.id.goal_projection_remaining_daily)

    fun bindView(goal: RunningGoal) {
        val context = this.itemView.context
        val projection = goal.projection
        perDayIdeal.setValues(projection.distancePerDayPeriod.display(true), context.getString(R.string.period_daily_distance))
        if (goal.expired()) {
            daysTotal.visibility = View.GONE
        } else {
            daysTotal.setValues(projection.distancePerDayRemaining.display(), context.getString(R.string.remaining_daily_distance))
            daysTotal.visibility = View.VISIBLE
        }
    }
}