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
        lblName.text = runningGoal.name
        lblPeriod.text = "%s - %s (%s / %s days)".format(runningGoal.target.start, runningGoal.target.end, runningGoal.progress.daysLapsed, runningGoal.progress.daysTotal)
        lblCurrent.text = runningGoal.progress.distanceToday.display()
        lblDistance.text = "/ %s".format(runningGoal.target.distance.display())
        lblStatus.let {
            when (runningGoal.progress.status) {
                GoalStatus.ONGOING -> {
                    it.text = "Ongoing"; it.backgroundTintList = this.itemView.context.getColorStateList(R.color.status_complete)
                }
                GoalStatus.NOT_STARTED -> {
                    it.text = "Not started"; it.backgroundTintList = this.itemView.context.getColorStateList(R.color.status_incomplete)
                }
                GoalStatus.EXPIRED -> {
                    it.text = "Expired"; it.backgroundTintList = this.itemView.context.getColorStateList(R.color.status_error)
                }
                GoalStatus.UNKNOWN -> {
                    it.text = "Unknown"; it.backgroundTintList = this.itemView.context.getColorStateList(R.color.status_incomplete)
                }
            }
        }
        progressUpdating.visibility = if (runningGoal.view.updating) View.VISIBLE else View.GONE
        itemView.setOnClickListener {
            listener.onItemClick(runningGoal)
        }
    }
}