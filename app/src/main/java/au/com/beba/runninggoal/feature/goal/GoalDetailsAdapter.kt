package au.com.beba.runninggoal.feature.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.feature.goal.viewholder.EmptyViewHolder
import au.com.beba.runninggoal.feature.goal.viewholder.GoalPeriodViewHolder
import au.com.beba.runninggoal.feature.goal.viewholder.GoalPositionViewHolder
import au.com.beba.runninggoal.feature.goal.viewholder.GoalProjectionViewHolder
import au.com.beba.runninggoal.feature.goal.viewholder.GoalWorkoutsSummaryViewHolder
import timber.log.Timber


class GoalDetailsAdapter
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var goal: RunningGoal? = null
    private var workouts: MutableList<Workout> = mutableListOf()

    private val EMPTY = -1
    private val PERIOD_DAYS = 100
    private val POSITION = 200
    private val PROJECTION = 300
    private val WORKOUTS = 400

    private val goalCells = arrayOf(PERIOD_DAYS, POSITION, PROJECTION, WORKOUTS)

    override fun getItemViewType(position: Int): Int {
        return when {
            (goal != null) && (workouts.size > 0) && (position <= goalCells.size) -> return goalCells[position]
            (goal != null) && (workouts.size == 0) && (position < goalCells.lastIndex) -> return goalCells[position]
            (workouts.size > 0) && (position == goalCells.lastIndex) -> return goalCells[position]
            else -> EMPTY
        }
    }

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PERIOD_DAYS -> GoalPeriodViewHolder(inflater.inflate(GoalPeriodViewHolder.layoutId, parent, false))
            POSITION -> GoalPositionViewHolder(inflater.inflate(GoalPositionViewHolder.layoutId, parent, false))
            PROJECTION -> GoalProjectionViewHolder(inflater.inflate(GoalProjectionViewHolder.layoutId, parent, false))
            WORKOUTS -> GoalWorkoutsSummaryViewHolder(inflater.inflate(GoalWorkoutsSummaryViewHolder.layoutId, parent, false))
            else -> EmptyViewHolder(inflater.inflate(EmptyViewHolder.layoutId, parent, false))
        }
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GoalPeriodViewHolder -> holder.bindView(goal!!.progress)
            is GoalPositionViewHolder -> holder.bindView(goal!!.progress)
            is GoalProjectionViewHolder -> holder.bindView(goal!!)
            is GoalWorkoutsSummaryViewHolder -> holder.bindView(workouts)
            is EmptyViewHolder -> holder.bindView("Stay tuned!")
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return goalCells.size
    }

    fun setGoal(goal: RunningGoal) {
        Timber.i("setGoal")
        this.goal = goal.copy()
    }

    fun setWorkout(workouts: List<Workout>) {
        Timber.i("setWorkout")
        Timber.d("setWorkout | size=%s", workouts.size)
        this.workouts.clear()
        this.workouts.addAll(workouts)
    }
}