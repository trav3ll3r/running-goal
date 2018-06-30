package au.com.beba.runninggoal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import au.com.beba.runninggoal.feature.GoalActivity
import au.com.beba.runninggoal.feature.goals.RunningGoalsFragment
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.models.SyncSource
import org.jetbrains.anko.find


class MainActivity : AppCompatActivity(),
        SyncSourcesFragment.SyncSourceListener,
        RunningGoalsFragment.RunningGoalListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigation()

        bottomNavigationView.selectedItemId = R.id.action_running_goals
    }

    private fun initBottomNavigation() {
        bottomNavigationView = find(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_running_goals -> showRunningGoals()
                R.id.action_sync_sources -> showSyncSources()
            }
            true
        }
    }

    override fun onAddRunningGoal() {
        createNewGoal()
    }

    override fun onRunningGoalClicked(runningGoal: RunningGoal) {
        editRunningGoal(runningGoal)
    }

    override fun onSyncSourceClicked(syncSource: SyncSource) {
        editSyncSource(syncSource)
    }

    private fun showRunningGoals() {
        supportFragmentManager.beginTransaction().replace(R.id.content_container, RunningGoalsFragment()).commit()
    }

    private fun showSyncSources() {
        supportFragmentManager.beginTransaction().replace(R.id.content_container, SyncSourcesFragment()).commit()
    }

    private fun createNewGoal() {
        val intent = Intent(this, GoalActivity::class.java)
        startActivity(intent)
    }

    private fun editRunningGoal(runningGoal: RunningGoal) {
        val intent = GoalActivity.buildIntent(this, runningGoal.id)
        startActivity(intent)
    }

    private fun editSyncSource(syncSource: SyncSource) {
        val intent = EditSyncSourceActivity.buildIntent(this, syncSource)
        startActivity(intent)
    }

/*
    private fun initFAB() {
        refreshFromDataSource()
    }

    private fun refreshFromDataSource() {
        Log.i(TAG, "refreshFromDataSource")
        val jobId = 1000

        val ctx = this
        launch {
            val goals = goalRepository.getGoals()
            goals.forEach {
                // Starts the JobIntentService
                ApiSourceIntentService.enqueueWork(ctx, ApiSourceIntentService.buildIntent(it.id, "STRAVA"), jobId)
            }
        }
    }
*/
}