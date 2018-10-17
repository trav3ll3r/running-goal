package au.com.beba.runninggoal.feature.goal.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.NumericWithLabel
import au.com.beba.runninggoal.domain.GoalProgress
import au.com.beba.runninggoal.domain.core.displaySigned
import au.com.beba.runninggoal.feature.widget.DecimalRenderer
import org.jetbrains.anko.find


class GoalPositionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val layoutId = R.layout.list_item_goal_position_ideal
    }

    private val label: TextView = itemView.find(R.id.label)
    private val positionInDistance: NumericWithLabel = itemView.find(R.id.goal_position_distance)
    private val positionInDays: NumericWithLabel = itemView.find(R.id.goal_position_ideal_days)

    fun bindView(progress: GoalProgress) {
        val context = this.itemView.context
        positionInDistance.setValues(
                progress.positionInDistance.displaySigned(),
                context.getString(R.string.unit_kilometre))

        positionInDays.setValues(
                DecimalRenderer.fromFloat(progress.positionInDays, true),
                context.getString(R.string.position_ideal_days))

    }
}