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
import au.com.beba.runninggoal.feature.GoalActivity
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import au.com.beba.runninggoal.feature.goals.RunningGoalsAdapter
import au.com.beba.runninggoal.networking.source.ApiSource
import au.com.beba.runninggoal.networking.source.StravaApiSource
import au.com.beba.runninggoal.repo.GoalRepo
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import java.time.LocalDate
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    @Inject
    lateinit var localPreferences: LocalPreferences

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerView.Adapter<GoalViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFAB()

        populateGoals()
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

    private fun refreshFromDataSource() {
        Log.i(TAG, "refreshFromDataSource")

        // TODO: FINISH OFF
        //val source: ApiSource = StravaApiSource()
//        launch(UI) {
//            val distance = source.getDistanceForDateRange(LocalDate.now(), LocalDate.now())
//            Log.d(TAG, "refreshFromDataSource | distance=$distance")
//        }
    }

    private fun populateGoals() {
        recyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val decoration = DividerItemDecoration(applicationContext, VERTICAL)
        recyclerView.addItemDecoration(decoration)

        val ctx = this
        async(UI) {
            val goals = GoalRepo.getInstance(ctx).getGoals()
            recyclerAdapter = RunningGoalsAdapter(goals)
            recyclerView.adapter = recyclerAdapter
        }
    }
}