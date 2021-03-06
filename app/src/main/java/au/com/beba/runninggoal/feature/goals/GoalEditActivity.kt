package au.com.beba.runninggoal.feature.goals

import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.Distance
import au.com.beba.runninggoal.domain.GoalDate
import au.com.beba.runninggoal.domain.GoalTarget
import au.com.beba.runninggoal.domain.Period
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.feature.appevents.GoalChangeEvent
import au.com.beba.runninggoal.feature.appevents.GoalDeleteEvent
import au.com.beba.runninggoal.feature.goal.GoalFeature
import au.com.beba.runninggoal.feature.widget.WidgetFeature
import au.com.beba.runninggoal.feature.workout.WorkoutFeature
import au.com.beba.runninggoal.ui.component.DistancePickerDialog
import au.com.beba.runninggoal.ui.component.display
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class GoalEditActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"

        fun buildIntent(context: Context, goalId: Long?): Intent {
            val intent = Intent(context, GoalEditActivity::class.java)
            if (goalId != null) {
                intent.putExtra(EXTRA_GOAL_ID, goalId)
            }
            return intent
        }
    }

    private var appWidgetId: Int = -1

    @Inject
    lateinit var goalFeature: GoalFeature
    @Inject
    lateinit var widgetFeature: WidgetFeature
    @Inject
    lateinit var workoutFeature: WorkoutFeature
    @Inject
    lateinit var eventCentre: PublisherEventCentre

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
            Timber.d("extractIntentData | goalId=%s".format(goalId))
            Timber.d("extractIntentData | appWidgetId=%s".format(appWidgetId))
        }

        return goalId
    }

    private fun resolveGoal(goalId: Long): RunningGoal {
        Timber.i("resolveGoal")

        var goal: RunningGoal? = null

        if (goalId > 0) {
            Timber.d("resolveGoal | from goalId")
            goal = goalFeature.getById(goalId)
        }

        if (goal == null) {
            goal = resolveFromWidgetId()
        }


        return goal ?: RunningGoal()
    }

    private fun resolveFromWidgetId(): RunningGoal? {
        if (appWidgetId > AppWidgetManager.INVALID_APPWIDGET_ID) {
            val widget = widgetFeature.getById(appWidgetId)
            if (widget != null) {
                Timber.d("resolveGoal | linked to widgetId")
                return goalFeature.getById(widget.goalId)
            }
        }

        return null
    }

    private fun initForm() {
        initDistancePicker(find(R.id.goal_distance))
        initDistancePicker(find(R.id.current_distance))

        initDatePicker(find(R.id.goal_start))
        initDatePicker(find(R.id.goal_end))
    }

    private fun populateGoal(goal: RunningGoal) = async(UI) {
        val goalName = goal.name
        val distance = goal.target.distance.display()
        val currentDistance = goal.progress.distanceToday.display()
        val startDate = (goal.target.period.from)
        val endDate = (goal.target.period.to)

        find<TextView>(R.id.goal_name).setText(goalName, TextView.BufferType.EDITABLE)
        find<TextView>(R.id.goal_distance).setText(distance, TextView.BufferType.EDITABLE)
        find<TextView>(R.id.current_distance).setText(currentDistance, TextView.BufferType.EDITABLE)
        find<TextView>(R.id.goal_start).setText(startDate.asDisplayLocalLong(), TextView.BufferType.EDITABLE)
        find<TextView>(R.id.goal_end).setText(endDate.asDisplayLocalLong(), TextView.BufferType.EDITABLE)
    }

    //TODO: MOVE TO VIEWMODEL
    private fun saveGoal(goal: RunningGoal) = async(UI) {
        Timber.i("saveGoal")
        val startDate = GoalDate(find<TextView>(R.id.goal_start).text.toString(), GoalDate.EARLIEST)
        val endDate = GoalDate(find<TextView>(R.id.goal_end).text.toString())

        goal.name = find<TextView>(R.id.goal_name).text.toString()
        goal.target = GoalTarget(
                Distance(find<TextView>(R.id.goal_distance).text.toString()),
                Period(startDate, endDate)
        )
        goal.progress.distanceToday = Distance(find<TextView>(R.id.current_distance).text.toString())

        val goalId = goalFeature.save(goal)

        eventCentre.publish(GoalChangeEvent(goalId))

        exit()
    }

    //TODO: MOVE TO VIEWMODEL
    private fun deleteGoal(runningGoal: RunningGoal) {
        Timber.i("deleteGoal")
        Timber.d("deleteGoal | goalId=%s".format(runningGoal.id))

        // IF DELETING runningGoal IS SUCCESSFUL
        if (goalFeature.delete(runningGoal) == 1) {
            // DELETE ALL RELATED Workouts
            workoutFeature.deleteAllForGoal(runningGoal.id)
            eventCentre.publish(GoalDeleteEvent(runningGoal.id))
        }

        exit()
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
            val selectedDate = GoalDate(year, monthOfYear + 1, dayOfMonth)
            editText.setText(selectedDate.asDisplayLocalLong())
        }

        editText.setOnClickListener {
            val currentDate = GoalDate(editText.text.toString())
            val dpd = DatePickerDialog(
                    this,
                    dateListener,
                    currentDate.yearLocal,
                    currentDate.monthLocal - 1,
                    currentDate.dayOfMonthLocal)
            dpd.datePicker.firstDayOfWeek = Calendar.MONDAY
            dpd.show()
        }
    }

    private fun exit() {
        finish()
    }
}
