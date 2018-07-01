package au.com.beba.runninggoal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import au.com.beba.runninggoal.feature.GoalActivity
import au.com.beba.runninggoal.feature.goals.RunningGoalsFragment
import au.com.beba.runninggoal.feature.progressSync.ApiSourceIntentService
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import javax.inject.Inject


class MainActivity : AppCompatActivity(),
        SyncSourcesFragment.SyncSourceListener,
        RunningGoalsFragment.RunningGoalListener {

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigation()

        bottomNavigationView.selectedItemId = R.id.action_running_goals
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_sync_now -> {
                syncNow()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun syncNow() {
        Log.i(TAG, "syncNow")
        val jobId = 1000

        val ctx = this
        launch {
            val syncSource = syncSourceRepository.getDefaultSyncSource()
            if (syncSource.isDefault) {
                val goals = goalRepository.getGoals()
                goals.forEach {
                    // Enqueues new JobIntentService
                    ApiSourceIntentService.enqueueWork(ctx, ApiSourceIntentService.buildIntent(it.id), jobId)
                }
            } else {
                launch(UI) {
                    // NOTIFY USER ABOUT MISSING DEFAULT SYNC SOURCE
                    val dialog = AlertDialog.Builder(ctx)
                            .setCancelable(true)
                            .setTitle(R.string.sync_now_error)
                            .setMessage(R.string.message_no_default_sync_sources)
                            .setPositiveButton(android.R.string.ok, null)
                            .create()

                    if (!isFinishing) {
                        dialog.show()
                    }
                }
            }
        }
    }
}