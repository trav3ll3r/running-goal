package au.com.beba.runninggoal.feature.widget.selectgoal

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.widget.R
import au.com.beba.runninggoal.feature.widget.WidgetFeature
import au.com.beba.runninggoal.feature.widget.WidgetFeatureImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SelectGoalActivity
    : AppCompatActivity(), ListListener<RunningGoal> {

    companion object {
        private val TAG = SelectGoalActivity::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val widgetFeature: WidgetFeature by lazy {
        val feature = WidgetFeatureImpl
        feature.bootstrap(this)
        feature
    }

    private var appWidgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
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
            logger.debug("extractIntentData | widgetId=%s".format(widgetId))
        }

        return widgetId
    }

    override fun onItemClick(item: RunningGoal) {
        bindGoalToWidget(item, appWidgetId)
    }

    private fun showRunningGoals() {
        supportFragmentManager.beginTransaction().replace(R.id.content_container, SelectGoalFragment()).commit()
    }

    private fun bindGoalToWidget(runningGoal: RunningGoal, appWidgetId: Int) {
        logger.info("bindGoalToWidget")

        widgetFeature.pairWithGoal(runningGoal.id, appWidgetId)
        closeWidgetConfig()
    }

    /**
     * Close the widget-handling Activity by setting result
     * as Activity.RESULT_OK and extra value of [appWidgetId]
     *
     * NOTE: Launched by system via [Context#startActivityForResult]
     */
    private fun closeWidgetConfig() {
        logger.info("closeWidgetConfig")
        logger.info("closeWidgetConfig | appWidgetId=$appWidgetId")
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}