package au.com.beba.runninggoal.feature.goals

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal
import org.jetbrains.anko.find

class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblName: TextView
    private val lblPeriod: TextView
    private val lblDistance: TextView
    private val lblCurrent: TextView

    init {
        lblName = itemView.find(R.id.goal_item_name)
        lblPeriod = itemView.find(R.id.goal_item_goal_period)
        lblDistance = itemView.find(R.id.goal_item_goal_distance)
        lblCurrent = itemView.find(R.id.goal_item_current_distance)
    }

    fun bindView(runningGoal: RunningGoal) {
        lblName.text = runningGoal.name
        lblPeriod.text = "%s - %s (%s / %s days)".format(runningGoal.target.start, runningGoal.target.end, runningGoal.progress.daysLapsed, runningGoal.progress.daysTotal)
        lblCurrent.text = runningGoal.progress.distanceToday.display()
        lblDistance.text = "/ %s".format(runningGoal.target.distance.display())
    }
}