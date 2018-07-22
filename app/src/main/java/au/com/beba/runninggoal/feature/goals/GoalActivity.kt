package au.com.beba.runninggoal.feature.goals

import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.DistancePickerDialog
import au.com.beba.runninggoal.feature.widget.GoalWidgetUpdater
import au.com.beba.runninggoal.launchSilent
import au.com.beba.runninggoal.models.Distance
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.Period
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.WidgetRepository
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_goal.*
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class GoalActivity : AppCompatActivity() {

    companion object {
        private val TAG = GoalActivity::class.java.simpleName
        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"

        fun buildIntent(context: Context, goalId: Long): Intent {
            val intent = Intent(context, GoalActivity::class.java)
            intent.putExtra(EXTRA_GOAL_ID, goalId)
            return intent
        }
    }

    private var appWidgetId: Int = -1

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var widgetRepository: WidgetRepository
    @Inject
    lateinit var goalWidgetUpdater: GoalWidgetUpdater

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val goalId = extractIntentData(intent)

        initForm()

        launch(UI) {
            val goal = resolveGoal(goalId)
            populateGoal(goal)
            findViewById<View>(R.id.btn_ok).setOnClickListener { saveGoal(goal) }
            findViewById<View>(R.id.btn_delete).setOnClickListener { deleteGoal(goal) }
        }
    }

    private fun extractIntentData(intent: Intent): Long {
        var goalId: Long = 0
        val extras = intent.extras
        if (extras != null) {
            goalId = extras.getLong(EXTRA_GOAL_ID, 0)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            Log.d(TAG, "extractIntentData | goalId=%s".format(goalId))
            Log.d(TAG, "extractIntentData | appWidgetId=%s".format(appWidgetId))
        }

        return goalId
    }

    private suspend fun resolveGoal(goalId: Long): RunningGoal {
        Log.i(TAG, "resolveGoal")
        val goal: RunningGoal? = withContext(DefaultDispatcher) {
            if (goalId > 0) {
                Log.d(TAG, "resolveGoal | from goalId")
                goalRepository.getById(goalId)
            } else {
                val widget = widgetRepository.getByWidgetId(appWidgetId)
                if (widget != null) {
                    Log.d(TAG, "resolveGoal | linked to widgetId")
                    goalRepository.getById(widget.goalId)
                } else {
                    null
                }
            }
        }

        return goal ?: RunningGoal()
    }


    private fun initForm() {
        initDistancePicker(findViewById(R.id.goal_distance))
        initDistancePicker(findViewById(R.id.current_distance))

        initDatePicker(findViewById(R.id.goal_start))
        initDatePicker(findViewById(R.id.goal_end))
    }

    private fun populateGoal(goal: RunningGoal) = launchSilent(UI) {
        val goalName = goal.name
        val distance = goal.target.distance.display()
        val currentDistance = goal.progress.distanceToday.display()
        val startDate = (goal.target.period.from)
        val endDate = (goal.target.period.to)

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
                Period(startDate, endDate)
        )
        goal.progress.distanceToday = Distance(current_distance.text.toString())

        val goalId = goalRepository.save(goal)

        // CHECK IF THIS IS NEEDED, USE goal
        val updatedGoal = goalRepository.getById(goalId)

        updateWidgetView(updatedGoal)

        exit()
    }

    private fun deleteGoal(runningGoal: RunningGoal) = launchSilent(DefaultDispatcher) {
        Log.i(TAG, "deleteGoal")

        Log.d(TAG, "deleteGoal | goalId=%s".format(runningGoal.id))
        if (goalRepository.delete(runningGoal) == 1) {
            // DELETE ALL RELATED Widgets IF DELETING runningGoal IS SUCCESSFUL
            runningGoal.deleted = true

            updateWidgetView(runningGoal)
        }

        exit()
    }

    private suspend fun updateWidgetView(runningGoal: RunningGoal?) = withContext(DefaultDispatcher) {
        Log.i(TAG, "updateWidgetView")
        val context = this
        if (runningGoal != null) {
            runBlocking {
                goalWidgetUpdater.updateAllWidgetsForGoal(context, runningGoal)
            }
        }
    }

    private fun initDistancePicker(editText: EditText) {
        val distanceSetListener: DistancePickerDialog.OnDistanceSetListener = object : DistancePickerDialog.OnDistanceSetListener {
            override fun onSetValue(distance: Distance) {
                editText.setText(distance.display())
            }
        }

        editText.setOnClickListener {
            val dpd = DistancePickerDialog(this, distanceSetListener)
            dpd.show(Distance(editText.text.toString()))
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

    private fun exit() {
        finish()
    }
}
