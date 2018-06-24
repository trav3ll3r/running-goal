package au.com.beba.runninggoal.feature.goals

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal
import org.jetbrains.anko.find


class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblName: TextView = itemView.find(R.id.goal_item_name)
    private val lblPeriod: TextView = itemView.find(R.id.goal_item_goal_period)
    private val lblDistance: TextView = itemView.find(R.id.goal_item_goal_distance)
    private val lblCurrent: TextView = itemView.find(R.id.goal_item_current_distance)

    fun bindView(runningGoal: RunningGoal) {
        lblName.text = runningGoal.name
        lblPeriod.text = "%s - %s (%s / %s days)".format(runningGoal.target.start, runningGoal.target.end, runningGoal.progress.daysLapsed, runningGoal.progress.daysTotal)
        lblCurrent.text = runningGoal.progress.distanceToday.display()
        lblDistance.text = "/ %s".format(runningGoal.target.distance.display())
    }
}