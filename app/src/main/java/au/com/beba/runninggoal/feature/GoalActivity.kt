package au.com.beba.runninggoal.feature

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.GoalProgress
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepo
import kotlinx.android.synthetic.main.activity_goal.*
import java.time.LocalDate


class GoalActivity : AppCompatActivity() {

    private var appWidgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        extractIntentData(intent)

        val goal = GoalRepo.getGoalForWidget(appWidgetId)

        populateGoal(goal)
        findViewById<View>(R.id.btn_ok).setOnClickListener { saveGoal() }
    }

    private fun extractIntentData(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }
    }

    private fun populateGoal(goal: RunningGoal?) {
        val goalName = goal?.name ?: "Goal"
        val distance = (goal?.target?.distance ?: 0).toString()
        val currentDistance = (goal?.progress?.distanceToday?: 0).toString()

        goal_name.setText(goalName, TextView.BufferType.EDITABLE)
        goal_distance.setText(distance, TextView.BufferType.EDITABLE)
        current_distance.setText(currentDistance, TextView.BufferType.EDITABLE)
    }

    private fun saveGoal() {
        val goal = RunningGoal(appWidgetId,
                goal_name.text.toString(),
                GoalTarget((goal_distance.text.toString()).toInt(),
                        LocalDate.of(2018, 6, 1),        //FIXME
                        LocalDate.of(2018, 6, 30)        //FIXME
                ),
                GoalProgress((current_distance.text.toString()).toDouble()))

        GoalRepo.save(goal, appWidgetId)

        val updatedGoal = GoalRepo.getGoalForWidget(appWidgetId)

        updateWidgetView(updatedGoal!!)
    }

    private fun updateWidgetView(runningGoal: RunningGoal) {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val rootView = RemoteViews(packageName, R.layout.goal_widget)

        GoalWidgetRenderer.updateUi(this, rootView, runningGoal)

        appWidgetManager.updateAppWidget(appWidgetId, rootView)

        closeWidgetConfig()
    }

    private fun closeWidgetConfig() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
