package au.com.beba.runninggoal.feature.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.workout.Workout
import timber.log.Timber


class WorkoutsAdapter(private val items: MutableList<Workout>)
    : RecyclerView.Adapter<WorkoutViewHolder>() {

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_workout, parent, false)
        return WorkoutViewHolder(v)
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): Workout {
        return items[position]
    }

    fun setItems(activities: List<Workout>) {
        Timber.i("setItems")
        Timber.d("setItems | activities=${activities.size}")
        items.clear()
        items.addAll(activities)
    }
}