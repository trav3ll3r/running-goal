package au.com.beba.runninggoal.feature.syncSources

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.progressSync.ApiSourceIntentService
import au.com.beba.runninggoal.feature.progressSync.SyncSourcesAdapter
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import javax.inject.Inject


class SyncSourcesActivity : AppCompatActivity() {

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository

    companion object {
        private val TAG = SyncSourcesActivity::class.java.simpleName
    }

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: SyncSourcesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_sources)

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
            refreshFromDataSource()
        }
    }

    private fun initRecyclerView() {
        recyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = SyncSourcesAdapter(mutableListOf())
        recyclerView.adapter = recyclerAdapter
    }

    private fun refreshList() {
        Log.i(TAG, "refreshList")
        launch(UI) {
            val syncSources = syncSourceRepository.getSyncSources()
            recyclerAdapter.setItems(syncSources)
            recyclerAdapter.notifyDataSetChanged()
        }
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
}