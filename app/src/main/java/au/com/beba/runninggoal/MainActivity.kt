package au.com.beba.runninggoal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import au.com.beba.runninggoal.feature.goal.GoalActionListener
import au.com.beba.runninggoal.feature.goals.GoalActivity
import au.com.beba.runninggoal.feature.goals.GoalDetailsFragment
import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import au.com.beba.runninggoal.feature.goals.RunningGoalsFragment
import au.com.beba.runninggoal.feature.progressSync.SyncSourceIntentService
import au.com.beba.runninggoal.feature.router.NavigationInteractor
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import au.com.beba.runninggoal.domain.core.RunningGoal
import au.com.beba.runninggoal.domain.sync.SyncSource
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity(),
        SyncSourcesFragment.SyncSourceListener,
        GoalActionListener,
        NavigationInteractor {

    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showRunningGoals()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_sync_all -> {
                syncAllGoals()
                return true
            }
            R.id.action_create_goal -> {
                createGoal()
                return true
            }
            R.id.action_manage_sync_sources -> {
                showSyncSources()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun createGoal() {
        gotoCreateGoal()
    }

    override fun editGoal(runningGoal: RunningGoal) {
        gotoEditGoal(runningGoal)
    }

    override fun syncGoal(runningGoal: RunningGoal) {
        syncOneGoal(runningGoal)
    }

    override fun showGoalDetails(runningGoal: RunningGoal, holder: GoalViewHolder) {
        gotoGoalDetails(runningGoal, holder)
    }

    override fun onSyncSourceClicked(syncSource: SyncSource) {
        editSyncSource(syncSource)
    }

    override fun onSyncSourcesRequested() {
        showSyncSources()
    }

    private fun showRunningGoals() {
        supportFragmentManager.beginTransaction().replace(R.id.content_container, RunningGoalsFragment()).commit()
    }

    private fun gotoGoalDetails(runningGoal: RunningGoal, holder: GoalViewHolder) {
        supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content_container, GoalDetailsFragment.newInstance(runningGoal).apply {
                    this.sharedElementEnterTransition = DetailsTransition()
                    this.enterTransition = DetailsTransition()  //ChangeBounds() //Fade()
                    exitTransition = DetailsTransition()    //Fade()
                    this.sharedElementReturnTransition = DetailsTransition()
                })
                .addToBackStack(null)
                .apply {
                    holder.getSharedViews().forEach {
                        addSharedElement(it, it.transitionName)
                    }
                }
                .commit()
    }

    private fun showSyncSources() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, SyncSourcesFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun gotoCreateGoal() {
        val intent = Intent(this, GoalActivity::class.java)
        startActivity(intent)
    }

    private fun gotoEditGoal(runningGoal: RunningGoal) {
        val intent = GoalActivity.buildIntent(this, runningGoal.id)
        startActivity(intent)
    }

    private fun editSyncSource(syncSource: SyncSource) {
        val intent = EditSyncSourceActivity.buildIntent(this, syncSource)
        startActivity(intent)
    }

    private fun syncOneGoal(runningGoal: RunningGoal) {
        Log.i(TAG, "syncOneGoal")
        syncGoals(runningGoal, 1001)
    }

    private fun syncAllGoals() {
        Log.i(TAG, "syncAllGoals")
        syncGoals(null, 1000)
    }

    private fun syncGoals(runningGoal: RunningGoal?, jobId: Int) {
        Log.i(TAG, "syncGoals")
        val ctx = this
        launch {
            val syncSource = syncSourceRepository.getDefaultSyncSource()
            if (syncSource.isDefault) {
                // Enqueues new JobIntentService
                SyncSourceIntentService.enqueueWork(
                        ctx,
                        SyncSourceIntentService.buildIntent(runningGoal),
                        jobId)
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

class DetailsTransition : TransitionSet() {
    init {
        duration = 400
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())

//        ordering = ORDERING_SEQUENTIAL
//                addTransition(AutoTransition())

    }
}