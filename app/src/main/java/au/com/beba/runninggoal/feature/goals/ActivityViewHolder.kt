package au.com.beba.runninggoal.feature.goals

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.feature.base.ListListener
import au.com.beba.runninggoal.models.AthleteActivity


class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    private val lblName: TextView = itemView.find(R.id.goal_item_name)
//    private val periodProgress: NumericProgress = itemView.find(R.id.goal_period_progress)
//    private val distanceProgress: NumericProgress = itemView.find(R.id.goal_distance_progress)
//    private val status: TextView = itemView.find(R.id.goal_item_status)
    //private val progressUpdating: ProgressBar = itemView.find(R.id.goal_item_updating_status)

    fun bindView(runningGoal: AthleteActivity, listener: ListListener<AthleteActivity>?) {
        val context = this.itemView.context

//        lblName.text = runningGoal.name
//        periodProgress.setValues(runningGoal.progress.daysLapsed.toString(),
//                context.getString(R.string.progress_total_format, runningGoal.progress.daysTotal.toString()),
//                context.getString(R.string.unit_days))

//        context.getString(R.string.goal_period_summary,
//                runningGoal.target.period.from.asDisplayLocalLong(),
//                runningGoal.target.period.to.asDisplayLocalLong()
//                )

//        distanceProgress.setValues(runningGoal.progress.distanceToday.display(),
//                context.getString(R.string.progress_total_format, runningGoal.target.distance.display()),
//                context.getString(R.string.unit_kilometre)
//        )

//        progressUpdating.visibility = if (runningGoal.view.updating) View.VISIBLE else View.GONE
//        itemView.setOnClickListener {
//            listener.onItemClick(runningGoal)
//        }
    }
}