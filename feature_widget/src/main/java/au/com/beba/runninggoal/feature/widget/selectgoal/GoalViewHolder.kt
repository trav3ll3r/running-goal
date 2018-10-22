package au.com.beba.runninggoal.feature.widget.selectgoal

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.domain.GoalStatus
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.widget.R
import au.com.beba.runninggoal.ui.component.NumericProgress
import org.jetbrains.anko.find


class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        val layoutId = R.layout.list_item_select_goal
    }

    private val lblName: TextView = itemView.find(R.id.goal_item_name)
    private val status: TextView = itemView.find(R.id.goal_item_status)
    private val periodProgress: NumericProgress = itemView.find(R.id.goal_period_progress)
    private val distanceProgress: NumericProgress = itemView.find(R.id.goal_distance_progress)

    fun bindView(runningGoal: RunningGoal, listener: ListListener<GoalViewHolder>) {
        val context = this.itemView.context

        lblName.text = runningGoal.name

        status.backgroundTintList = when (runningGoal.progress.status) {
            GoalStatus.ONGOING -> context.getColorStateList(R.color.status_ongoing)
            GoalStatus.NOT_STARTED -> context.getColorStateList(R.color.status_not_started)
            GoalStatus.EXPIRED -> context.getColorStateList(R.color.status_expired)
            GoalStatus.UNKNOWN -> context.getColorStateList(R.color.status_not_started)
        }

//        periodProgress.setValues(runningGoal.progress.daysLapsed.toString(),
//                context.getString(R.string.progress_total_format, runningGoal.progress.daysTotal.toString()),
//                context.getString(R.string.unit_days))

//        context.getString(R.string.goal_period_summary,
//                runningGoal.target.period.from.asDisplayLocalLong(),
//                runningGoal.target.period.to.asDisplayLocalLong()
//                )

//        distanceProgress.setValues(runningGoal.progress.distanceToday.display(),
//                context.getString(R.string.progress_total_format, runningGoal.target.distance.display()),
//                context.getString(R.string.unit_kilometre)
//        )

        itemView.tag = runningGoal
        itemView.setOnClickListener {
            listener.onItemClick(this)
        }
    }
}