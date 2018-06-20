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
import au.com.beba.runninggoal.component.DistancePickerDialog
import au.com.beba.runninggoal.feature.widget.GoalWidgetRenderer
import au.com.beba.runninggoal.launchSilent
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepo
import au.com.beba.runninggoal.repo.GoalRepository
import kotlinx.android.synthetic.main.activity_goal.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class GoalActivity : AppCompatActivity() {

    companion object {
        private val TAG = GoalActivity::class.java.simpleName
    }

    private var appWidgetId: Int = -1
    private lateinit var goalRepository: GoalRepository

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        extractIntentData(intent)

        goalRepository = GoalRepo.getInstance(this)

        @Suppress("DeferredResultUnused")
        async(UI) {
            val goal = goalRepository.getGoalForWidget(appWidgetId)
            populateGoal(goal)
            findViewById<View>(R.id.btn_ok).setOnClickListener { saveGoal(goal) }
        }
    }

    private fun extractIntentData(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            Log.d(TAG, "extractIntentData | appWidgetId=%s".format(appWidgetId))
        }

        initDistancePicker(findViewById(R.id.goal_distance))
        initDistancePicker(findViewById(R.id.current_distance))

        initDatePicker(findViewById(R.id.goal_start))
        initDatePicker(findViewById(R.id.goal_end))
    }

    private fun populateGoal(goal: RunningGoal) = launchSilent(UI) {
        val goalName = goal.name
        val distance = goal.target.distance.display()
        val currentDistance = goal.progress.distanceToday.display()
        val startDate = (goal.target.start)
        val endDate = (goal.target.end)

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

    private fun saveGoal(goal: RunningGoal) = launchSilent(UI) {
        Log.i(TAG, "saveGoal")
        val startDate = parseDate(goal_start.text.toString())
        val endDate = parseDate(goal_end.text.toString())

        goal.name = goal_name.text.toString()
        goal.target = GoalTarget(
                Distance(goal_distance.text.toString()),
                startDate,
                endDate
        )
        goal.progress.distanceToday = Distance(current_distance.text.toString())

        Log.d(TAG, "saveGoal | appWidgetId=%s".format(appWidgetId))
        goalRepository.save(goal, appWidgetId)

        val updatedGoal = goalRepository.getGoalForWidget(appWidgetId)

        updateWidgetView(updatedGoal)
    }

    private fun updateWidgetView(runningGoal: RunningGoal) {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val rootView = RemoteViews(packageName, R.layout.goal_widget)

        GoalWidgetRenderer.updateUi(this, rootView, runningGoal)

        Log.i(TAG, "updateWidgetView | appWidgetId=%s".format(appWidgetId))
        appWidgetManager.updateAppWidget(appWidgetId, rootView)

        closeWidgetConfig()
    }

    private fun closeWidgetConfig() {
        Log.i(TAG, "closeWidgetConfig | appWidgetId=%s".format(appWidgetId))
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun initDistancePicker(editText: EditText) {
        val distanceSetListener: DistancePickerDialog.OnDistanceSetListener
//        val distanceSetListener: DistancePickerDialog.OnDistanceSetListener = object DistancePickerDialog.OnDistanceSetListener { distance ->
//            editText.setText(distance.display())
//        }

        editText.setOnClickListener {
//            val dpd = DistancePickerDialog(this, distanceSetListener)
//            dpd.show(Distance(editText.text.toString()))
        }
    }

    private fun initDatePicker(editText: EditText) {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            editText.setText(dateFormatter.format(LocalDate.of(year, monthOfYear + 1, dayOfMonth)))
        }

        editText.setOnClickListener {
            val currentDate = parseDate(editText.text.toString())
            val dpd = DatePickerDialog(this, dateListener, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)
            dpd.datePicker.firstDayOfWeek = Calendar.MONDAY
            dpd.show()
        }
    }
}
