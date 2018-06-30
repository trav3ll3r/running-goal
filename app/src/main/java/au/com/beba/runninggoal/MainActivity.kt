package au.com.beba.runninggoal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import au.com.beba.runninggoal.feature.GoalActivity
import au.com.beba.runninggoal.feature.base.AdapterListener
import au.com.beba.runninggoal.feature.goals.RunningGoalsAdapter
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesActivity
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var goalRepository: GoalRepository

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RunningGoalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFAB()

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun initFAB() {
        fab = find(R.id.fab)
        fab.setOnClickListener {
            createNewGoal()
        }
    }

    private fun createNewGoal() {
        val intent = Intent(this, GoalActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.data_sources -> {
                showSyncSources()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSyncSources() {
        val intent = Intent(this, SyncSourcesActivity::class.java)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        recyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val decoration = DividerItemDecoration(applicationContext, VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = RunningGoalsAdapter(mutableListOf(), object : AdapterListener<RunningGoal> {
            override fun onItemClick(item: RunningGoal) {
                editRunningGoal(item)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun refreshList() {
        Log.i(TAG, "refreshList")
        launch(UI) {
            val goals = goalRepository.getGoals()
            recyclerAdapter.setItems(goals)
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun editRunningGoal(runningGoal: RunningGoal) {
        val intent = GoalActivity.buildIntent(this, runningGoal.id)
        startActivity(intent)
    }
}