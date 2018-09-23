package au.com.beba.runninggoal.feature.goals

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.base.AdapterListener
import au.com.beba.runninggoal.models.GoalStatus
import au.com.beba.runninggoal.models.RunningGoal
import org.jetbrains.anko.find


class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblName: TextView = itemView.find(R.id.goal_item_name)
    private val lblPeriod: TextView = itemView.find(R.id.goal_item_goal_period)
    private val lblDistance: TextView = itemView.find(R.id.goal_item_goal_distance)
    private val lblCurrent: TextView = itemView.find(R.id.goal_item_current_distance)
    private val lblStatus: TextView = itemView.find(R.id.sync_source_item_status)
    private val progressUpdating: ProgressBar = itemView.find(R.id.goal_item_updating_status)

    fun bindView(runningGoal: RunningGoal, listener: AdapterListener<RunningGoal>) {
        val context = this.itemView.context

        lblName.text = runningGoal.name
        lblPeriod.text = context.getString(R.string.goal_period_summary,
                runningGoal.target.period.from.asDisplayLocalLong(),
                runningGoal.target.period.to.asDisplayLocalLong(),
                runningGoal.progress.daysLapsed,
                runningGoal.progress.daysTotal)
        lblCurrent.text = runningGoal.progress.distanceToday.display()
        lblDistance.text = context.getString(R.string.goal_target_distance, runningGoal.target.distance.display())
        lblStatus.let {
            when (runningGoal.progress.status) {
                GoalStatus.ONGOING -> {
                    it.text = context.getString(R.string.status_ongoing); it.backgroundTintList = context.getColorStateList(R.color.status_complete)
                }
                GoalStatus.NOT_STARTED -> {
                    it.text = context.getString(R.string.status_not_started); it.backgroundTintList = context.getColorStateList(R.color.status_incomplete)
                }
                GoalStatus.EXPIRED -> {
                    it.text = context.getString(R.string.status_expired); it.backgroundTintList = context.getColorStateList(R.color.status_error)
                }
                GoalStatus.UNKNOWN -> {
                    it.text = context.getString(R.string.status_unknown); it.backgroundTintList = context.getColorStateList(R.color.status_incomplete)
                }
            }
        }
        progressUpdating.visibility = if (runningGoal.view.updating) View.VISIBLE else View.GONE
        itemView.setOnClickListener {
            listener.onItemClick(runningGoal)
        }
    }
}