package au.com.beba.runninggoal.feature

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RemoteViews
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.GoalTarget
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepo
import java.util.*


class GoalActivity : AppCompatActivity() {

    private var mAppWidgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        findViewById<View>(R.id.btn_ok).setOnClickListener { updateWidgetView(GoalRepo.getGoalById(1)) }
        configureWidget(intent)
    }

    private fun configureWidget(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }
    }

    private fun updateWidgetView(runningGoal: RunningGoal) {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val rootView = RemoteViews(packageName, R.layout.goal_widget)

        GoalWidgetUtil.updateUi(rootView, runningGoal)

        appWidgetManager.updateAppWidget(mAppWidgetId, rootView)

        // TODO: CALL ON BUTTON CLICK
        closeWidgetConfig()
    }

    private fun closeWidgetConfig() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
