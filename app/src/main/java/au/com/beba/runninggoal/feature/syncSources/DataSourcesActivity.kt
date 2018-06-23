package au.com.beba.runninggoal.feature.syncSources

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.progressSync.ApiSourceIntentService
import org.jetbrains.anko.find

class DataSourcesActivity : AppCompatActivity() {

    companion object {
        private val TAG = DataSourcesActivity::class.java.simpleName
    }

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_sources)

        initFAB()
    }

    private fun initFAB() {
        fab = find(R.id.fab)
        fab.setOnClickListener {
            refreshFromDataSource()
        }
    }

    private fun refreshFromDataSource() {
        Log.i(TAG, "refreshFromDataSource")
        val jobId = 1000

        // Starts the JobIntentService
        ApiSourceIntentService.enqueueWork(this, ApiSourceIntentService.buildIntent("STRAVA"), jobId)
    }
}