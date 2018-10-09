package au.com.beba.runninggoal.feature.goals

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.Workout
import au.com.beba.runninggoal.domain.core.GoalStatus
import au.com.beba.runninggoal.domain.core.RunningGoal
import au.com.beba.runninggoal.feature.goal.GoalActionListener
import au.com.beba.runninggoal.feature.goal.GoalViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_goal_details.*
import javax.inject.Inject


class GoalDetailsFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(GoalViewModel::class.java)
    }

    companion object {
        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"

        fun newInstance(runningGoal: RunningGoal): GoalDetailsFragment {

            val f = GoalDetailsFragment()
            val extra = Bundle()
            extra.putLong(EXTRA_GOAL_ID, runningGoal.id)
            f.arguments = extra
            return f
        }

        private val TAG = GoalDetailsFragment::class.java.simpleName
    }

    private val goalId: Long
        get() {
            return arguments?.getLong(EXTRA_GOAL_ID) ?: 0L
        }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: WorkoutsAdapter

    private var goalActionListener: GoalActionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = ChangeBounds()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTransitionNames(goalId)

        // View is created so postpone the transition
        postponeEnterTransition()

        initLiveData()

        initRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GoalActionListener) {
            goalActionListener = context
        } else {
            Log.d(TAG, context.toString() + " must implement %s".format(GoalActionListener::class.java.simpleName))
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.fetchGoal(goalId)
//        viewModel.fetchActivities(goalId)
    }

    override fun onDetach() {
        super.onDetach()
        goalActionListener = null
    }

    /* ************ */
    /* INITIALISERS */
    /* ************ */
    /**
     * Assign unique name for each element partaking in
     * Fragment Shared Element Transition
     */
    private fun initTransitionNames(id: Long) {
        goal_item_name.transitionName = "goal_name_%s".format(id)
        goal_item_status.transitionName = "goal_status_%s".format(id)
        goal_distance_current.transitionName = "goal_distance_current_%s".format(id)
        goal_days_lapsed.transitionName = "goal_period_current_%s".format(id)
    }

    private fun initLiveData() {
        viewModel.goalLiveData.observe(this, Observer {
            if (it != null) {
                handleGoalUpdate(it)
            }
        })
        viewModel.fetchGoal(goalId)

        viewModel.activitiesLiveData.observe(this, Observer {
            if (it != null) {
                updateList(it)
            }
        })
        viewModel.fetchActivities(goalId)
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = WorkoutsAdapter(mutableListOf())
        recyclerView.adapter = recyclerAdapter
    }

    /* ******* */
    /* ACTIONS */
    /* ******* */
    private fun editGoal(goal: RunningGoal) {
        goalActionListener?.editGoal(goal)
    }

    private fun syncGoal(goal: RunningGoal) {
        viewModel.syncWorkouts(context, goal)
    }

    /* ********* */
    /* REACTIONS */
    /* ********* */
    private fun handleGoalUpdate(runningGoal: RunningGoal) {
        Log.i(TAG, "handleGoalUpdate")
        val ctx = context
        if (ctx != null) {
            goal_item_name.text = runningGoal.name

            goal_item_status.let {
                when (runningGoal.progress.status) {
                    GoalStatus.ONGOING -> {
                        it.text = ctx.getString(R.string.status_ongoing); it.backgroundTintList = ctx.getColorStateList(R.color.status_ongoing)
                    }
                    GoalStatus.NOT_STARTED -> {
                        it.text = ctx.getString(R.string.status_not_started); it.backgroundTintList = ctx.getColorStateList(R.color.status_not_started)
                    }
                    GoalStatus.EXPIRED -> {
                        it.text = ctx.getString(R.string.status_expired); it.backgroundTintList = ctx.getColorStateList(R.color.status_expired)
                    }
                    GoalStatus.UNKNOWN -> {
                        it.text = ctx.getString(R.string.status_unknown); it.backgroundTintList = ctx.getColorStateList(R.color.status_not_started)
                    }
                }
            }

            goal_distance_current.setValues(runningGoal.progress.distanceToday.display(true), getString(R.string.current_units, runningGoal.progress.distanceToday.units))
            goal_distance_target.setValues(runningGoal.target.distance.display(true), getString(R.string.target_units, runningGoal.target.distance.units))

            goal_days_lapsed.setValues(runningGoal.progress.daysLapsed.toString(), ctx.getString(R.string.lapsed))
            goal_days_total.setValues(runningGoal.target.period.totalDays.toString(), ctx.getString(R.string.total))

            goal_progress_position.setValues(runningGoal.progress.positionInDistance.displaySigned(), ctx.getString(R.string.progress_position))

            goal_item_edit.setOnClickListener { editGoal(runningGoal) }
            goal_item_sync.setOnClickListener { syncGoal(runningGoal) }

            //handleBusyIndicator(runningGoal.view.updating)
        }

        // Data is loaded so lets wait for our parent to be drawn
        (view?.parent as? ViewGroup)?.doOnPreDraw { _ ->
            // Parent has been drawn. Start transitioning!
            startPostponedEnterTransition()
        }
    }

    private fun updateList(items: List<Workout>) {
        Log.i(TAG, "updateList")
        recyclerAdapter.setItems(items)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun handleBusyIndicator(busy: Boolean) {
        Log.i(TAG, "handleBusyIndicator")
        if (busy) {
            goal_item_sync.startAnimation(syncAnimation)
        } else {
            goal_item_sync.clearAnimation()
        }
    }

    private val syncAnimation: Animation
        get() {
            val r = RotateAnimation(
                    0f,
                    360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
            r.interpolator = LinearInterpolator()
            r.repeatCount = Animation.INFINITE
            r.duration = 900
            return r
        }
}
