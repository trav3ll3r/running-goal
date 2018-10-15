package au.com.beba.runninggoal.feature.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.base.ListListener
import timber.log.Timber


class RunningGoalsAdapter(private val items: MutableList<RunningGoal>, private val listener: ListListener<GoalViewHolder>) : RecyclerView.Adapter<GoalViewHolder>() {

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_goal, parent, false)
        return GoalViewHolder(v)
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bindView(getItem(position), listener)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): RunningGoal {
        return items[position]
    }

    fun setItems(goals: List<RunningGoal>) {
        Timber.i("setItems")
        Timber.d("setItems | goals=${goals.size}")
        items.clear()
        items.addAll(goals)
    }
}