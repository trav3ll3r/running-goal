package au.com.beba.runninggoal.feature

import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.RunningGoal

object GoalWidgetUtil {
    fun updateUi(rootView: RemoteViews, runningGoal: RunningGoal) {

        rootView.setTextViewText(R.id.goal_distance, "%s km".format(runningGoal.target.distance))

        val progress = runningGoal.progress
        if (progress != null) {
            rootView.setTextViewText(R.id.current_distance, "%s km".format(progress.distanceToday))
            rootView.setTextViewText(R.id.expected_distance_today, "%s km".format(progress.distanceExpected))
        }

        val projections = runningGoal.projection
        if (projections != null) {
            rootView.setTextViewText(R.id.project_distance_per_day, "%s km".format(projections.distancePerDay))

        }
    }
}