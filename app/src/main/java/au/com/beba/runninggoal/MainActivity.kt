package au.com.beba.runninggoal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import au.com.beba.runninggoal.feature.goals.RunningGoalsAdapter
import au.com.beba.runninggoal.repo.GoalRepo
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import javax.inject.Inject
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    @Inject
    lateinit var localPreferences: LocalPreferences

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerView.Adapter<GoalViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        populateGoals()
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