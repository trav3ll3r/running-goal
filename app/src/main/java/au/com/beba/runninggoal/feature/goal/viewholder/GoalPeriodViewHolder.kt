package au.com.beba.runninggoal.feature.goal.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.NumericWithLabel
import au.com.beba.runninggoal.domain.GoalProgress
import org.jetbrains.anko.find


class GoalPeriodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val layoutId = R.layout.list_item_goal_period

        fun factory(inflater: LayoutInflater, parent: ViewGroup): GoalPeriodViewHolder {
            return GoalPeriodViewHolder(inflater.inflate(GoalPeriodViewHolder.layoutId, parent, false))
        }
    }

    private val label: TextView = itemView.find(R.id.label)
    private val daysLapsed: NumericWithLabel = itemView.find(R.id.goal_days_lapsed)
    private val daysTotal: NumericWithLabel = itemView.find(R.id.goal_days_total)

    fun bindView(progress: GoalProgress) {
        val context = this.itemView.context
        daysLapsed.setValues(progress.daysLapsed.toString(),
                context.getString(R.string.lapsed))

        daysTotal.setValues(progress.daysTotal.toString(),
                context.getString(R.string.total))
    }
}