package au.com.beba.runninggoal.feature

import android.app.Activity
import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.GoalProgress
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepo
import kotlinx.android.synthetic.main.activity_goal.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class GoalActivity : AppCompatActivity() {

    companion object {
        private val TAG = GoalActivity::class.java.simpleName
    }

    private var appWidgetId: Int = -1

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

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
            Log.d(TAG, "extractIntentData | appWidgetId=%s".format(appWidgetId))
        }

        initDatePicker(findViewById(R.id.goal_start))
        initDatePicker(findViewById(R.id.goal_end))
    }

    private fun populateGoal(goal: RunningGoal?) {
        val goalName = goal?.name ?: "Goal"
        val distance = (goal?.target?.distance ?: 0).toString()
        val currentDistance = (goal?.progress?.distanceToday ?: 0).toString()
        val startDate = (goal?.target?.start ?: LocalDate.now())
        val endDate = (goal?.target?.end ?: LocalDate.now())

        goal_name.setText(goalName, TextView.BufferType.EDITABLE)
        goal_distance.setText(distance, TextView.BufferType.EDITABLE)
        current_distance.setText(currentDistance, TextView.BufferType.EDITABLE)
        goal_start.setText(formatDate(startDate), TextView.BufferType.EDITABLE)
        goal_end.setText(formatDate(endDate), TextView.BufferType.EDITABLE)
    }

    private fun parseDate(value: String): LocalDate {
        return LocalDate.parse(value, dateFormatter)
    }

    private fun formatDate(value: LocalDate): String {
        return dateFormatter.format(value)
    }

    private fun saveGoal() {
        val startDate = parseDate(goal_start.text.toString())
        val endDate = parseDate(goal_end.text.toString())

        val goal = RunningGoal(appWidgetId,
                goal_name.text.toString(),
                GoalTarget(
                        (goal_distance.text.toString()).toInt(),
                        startDate,
                        endDate
                ),
                GoalProgress((current_distance.text.toString()).toDouble()))

        Log.d(TAG, "saveGoal | appWidgetId=%s".format(appWidgetId))
        GoalRepo.save(goal, appWidgetId)

        val updatedGoal = GoalRepo.getGoalForWidget(appWidgetId)

        updateWidgetView(updatedGoal!!)
    }

    private fun updateWidgetView(runningGoal: RunningGoal) {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val rootView = RemoteViews(packageName, R.layout.goal_widget)

        GoalWidgetRenderer.updateUi(this, rootView, runningGoal)

        Log.d(TAG, "updateWidgetView | appWidgetId=%s".format(appWidgetId))
        appWidgetManager.updateAppWidget(appWidgetId, rootView)

        closeWidgetConfig()
    }

    private fun closeWidgetConfig() {
        Log.d(TAG, "closeWidgetConfig | appWidgetId=%s".format(appWidgetId))
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun initDatePicker(editText: EditText) {
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            editText.setText(dateFormatter.format(LocalDate.of(year, monthOfYear + 1, dayOfMonth)))
        }

        editText.setOnClickListener {
            val currentDate = parseDate(editText.text.toString())
            DatePickerDialog(this, date, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth).show()
        }
    }
}
