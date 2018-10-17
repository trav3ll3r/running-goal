package au.com.beba.runninggoal.feature.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.base.ListListener
import au.com.beba.runninggoal.feature.goals.GoalsListFragment
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.widget.WidgetRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class SelectGoalActivity : AppCompatActivity(),
        ListListener<RunningGoal> {

    companion object {
        private val TAG = SelectGoalActivity::class.java.simpleName
    }

    @Inject
    lateinit var widgetRepo: WidgetRepository
    @Inject
    lateinit var goalWidgetUpdater: GoalWidgetUpdater

    private var appWidgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_goal)

        appWidgetId = extractIntentData(intent)

        showRunningGoals()
    }

    private fun extractIntentData(intent: Intent): Int {
        var widgetId: Int = -1
        val extras = intent.extras
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            Log.d(TAG, "extractIntentData | widgetId=%s".format(widgetId))
        }

        return widgetId
    }

    override fun onItemClick(item: RunningGoal) {
        bindGoalToWidget(item, appWidgetId)
    }

    private fun showRunningGoals() {
        supportFragmentManager.beginTransaction().replace(R.id.content_container, GoalsListFragment()).commit()
    }

    private fun bindGoalToWidget(runningGoal: RunningGoal, appWidgetId: Int) {
        runBlocking {
            widgetRepo.pairWithGoal(runningGoal.id, appWidgetId)
            updateWidgetView(runningGoal)
            closeWidgetConfig()
        }
    }

    private fun closeWidgetConfig() {
        Log.i(TAG, "closeWidgetConfig")
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private suspend fun updateWidgetView(runningGoal: RunningGoal) = withContext(Dispatchers.Default) {
        goalWidgetUpdater.updateAllWidgetsForGoal(applicationContext, runningGoal)
    }
}