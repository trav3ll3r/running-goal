package au.com.beba.runninggoal.feature.goals

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal
import org.jetbrains.anko.find

class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblName: TextView
    private val lblDistance: TextView

    init {
        lblName = itemView.find(R.id.goal_item_name)
        lblDistance = itemView.find(R.id.goal_item_distance)
    }

    fun bindView(runningGoal: RunningGoal) {
        lblName.text = runningGoal.name
        lblDistance.text = runningGoal.target.distance.toString()
    }
}