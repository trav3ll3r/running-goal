package au.com.beba.runninggoal.feature.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.feature.base.ListListener
import timber.log.Timber


class RunningGoalsAdapter : RecyclerView.Adapter<GoalViewHolder>() {

    private val items: MutableList<RunningGoal> = mutableListOf()
    private val eventCentre: PublisherEventCentre

    init {
        eventCentre = EventCentre
    }

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(GoalViewHolder.layoutId, parent, false)
        return GoalViewHolder(v)
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bindView(getItem(position), object : ListListener<GoalViewHolder> {
            override fun onItemClick(item: GoalViewHolder) {
                eventCentre.publish(GoalViewHolder.GoalSelectedEvent(item.itemView.tag as RunningGoal, item))
            }
        })
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