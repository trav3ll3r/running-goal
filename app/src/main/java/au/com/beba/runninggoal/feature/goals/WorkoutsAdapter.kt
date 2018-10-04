package au.com.beba.runninggoal.feature.goals

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.AthleteActivity


class WorkoutsAdapter(private val items: MutableList<AthleteActivity>)
    : RecyclerView.Adapter<WorkoutViewHolder>() {

    companion object {
        private val TAG = WorkoutsAdapter::class.java.simpleName
    }

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

    private fun getItem(position: Int): AthleteActivity {
        return items[position]
    }

    fun setItems(activities: List<AthleteActivity>) {
        Log.i(TAG, "setItems")
        Log.d(TAG, "setItems | activities=${activities.size}")
        items.clear()
        items.addAll(activities)
    }
}