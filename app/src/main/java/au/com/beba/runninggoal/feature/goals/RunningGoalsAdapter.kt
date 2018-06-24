package au.com.beba.runninggoal.feature.goals

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal


class RunningGoalsAdapter(private val items: MutableList<RunningGoal>) : RecyclerView.Adapter<GoalViewHolder>() {

    companion object {
        private val TAG = RunningGoalsAdapter::class.java.simpleName
    }

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.goal_list_item, parent, false)
        return GoalViewHolder(v)
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): RunningGoal {
        return items[position]
    }

    fun setItems(goals: List<RunningGoal>) {
        Log.i(TAG, "setItems")
        Log.d(TAG, "setItems | goals=${goals.size}")
        items.clear()
        items.addAll(goals)
    }
}