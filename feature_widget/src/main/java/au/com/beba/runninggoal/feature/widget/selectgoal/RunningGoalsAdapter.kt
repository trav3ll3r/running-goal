package au.com.beba.runninggoal.feature.widget.selectgoal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.widget.R
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RunningGoalsAdapter(
        private val items: MutableList<RunningGoal>,
        private val listener: ListListener<GoalViewHolder>)
    : RecyclerView.Adapter<GoalViewHolder>() {

    companion object {
        private val TAG = RunningGoalsAdapter::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(GoalViewHolder.layoutId, parent, false)
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
        logger.info("setItems")
        logger.debug("setItems | goals=${goals.size}")
        items.clear()
        items.addAll(goals)
    }
}