package au.com.beba.runninggoal.feature.goals

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.NumericWithLabel
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.domain.Distance
import au.com.beba.runninggoal.domain.GoalDate
import au.com.beba.runninggoal.domain.core.display
import org.jetbrains.anko.find


class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name = itemView.find<TextView>(R.id.workout_name)
    private val description = itemView.find<TextView>(R.id.workout_description)
    private val dateTime = itemView.find<TextView>(R.id.workout_datetime)
    private val distance = itemView.find<NumericWithLabel>(R.id.workout_distance)

    fun bindView(workout: Workout) {
        val dt = GoalDate(workout.dateTime)
        val d = Distance.fromMetres(workout.distanceInMetres)

        name.text = workout.name
        description.text = workout.description
        dateTime.text = dt.asDisplayLocalLong()
        distance.setValues(d.display(true), d.units)
    }
}