package au.com.beba.runninggoal.feature.goals

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.NumericProgress
import au.com.beba.runninggoal.feature.base.ListListener
import au.com.beba.runninggoal.domain.GoalStatus
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.core.display
import org.jetbrains.anko.find


class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblName: TextView = itemView.find(R.id.goal_item_name)
    private val periodProgress: NumericProgress = itemView.find(R.id.goal_period_progress)
    private val distanceProgress: NumericProgress = itemView.find(R.id.goal_distance_progress)
    private val status: TextView = itemView.find(R.id.goal_item_status)
    private val progressUpdating: ProgressBar = itemView.find(R.id.goal_item_updating_status)

    fun bindView(runningGoal: RunningGoal, listener: ListListener<GoalViewHolder>) {
        val context = this.itemView.context

        lblName.text = runningGoal.name
        periodProgress.setValues(runningGoal.progress.daysLapsed.toString(),
                context.getString(R.string.progress_total_format, runningGoal.progress.daysTotal.toString()),
                context.getString(R.string.unit_days))

//        context.getString(R.string.goal_period_summary,
//                runningGoal.target.period.from.asDisplayLocalLong(),
//                runningGoal.target.period.to.asDisplayLocalLong()
//                )

        distanceProgress.setValues(runningGoal.progress.distanceToday.display(),
                context.getString(R.string.progress_total_format, runningGoal.target.distance.display()),
                context.getString(R.string.unit_kilometre)
        )

        status.backgroundTintList = when (runningGoal.progress.status) {
            GoalStatus.ONGOING -> context.getColorStateList(R.color.status_ongoing)
            GoalStatus.NOT_STARTED -> context.getColorStateList(R.color.status_not_started)
            GoalStatus.EXPIRED -> context.getColorStateList(R.color.status_expired)
            GoalStatus.UNKNOWN -> context.getColorStateList(R.color.status_not_started)
        }

        progressUpdating.visibility = if (runningGoal.view.updating) View.VISIBLE else View.GONE

        itemView.tag = runningGoal
        itemView.setOnClickListener {
            listener.onItemClick(this)
        }

        setTransitionNames(runningGoal)
    }

    fun getSharedViews(): List<View> {
        return listOf(lblName, status, distanceProgress, periodProgress)
    }

    /**
     * Assign unique name "per list item" for each element partaking in
     * Fragment Shared Element Transition
     */
    private fun setTransitionNames(runningGoal: RunningGoal) {
        val id = runningGoal.id
        lblName.transitionName = "goal_name_%s".format(id)
        status.transitionName = "goal_status_%s".format(id)
        distanceProgress.transitionName = "goal_distance_current_%s".format(id)
        periodProgress.transitionName = "goal_period_current_%s".format(id)
    }
}