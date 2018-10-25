package au.com.beba.runninggoal.feature.goal

import android.content.Context
import android.os.Bundle
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
import au.com.beba.runninggoal.domain.GoalStatus
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.ui.component.display
import au.com.beba.runninggoal.domain.workout.Workout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_goal_details.*
import timber.log.Timber
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
    }

    private val goalId: Long
        get() {
            return arguments?.getLong(EXTRA_GOAL_ID) ?: 0L
        }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: GoalDetailsAdapter

    private var goalActionListener: GoalActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        //TODO: sharedElementEnterTransition = ChangeBounds()
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
            Timber.d("%s must implement %s".format(context.toString(), GoalActionListener::class.java.simpleName))
        }
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
        goal_item_status_background.transitionName = "goal_status_%s".format(id)
        goal_distance_current.transitionName = "goal_distance_current_%s".format(id)
    }

    private fun initLiveData() {
        Timber.i("initLiveData")
        viewModel.getObservableGoal(goalId).observe(this, Observer {
            Timber.i("initLiveData | goalsLiveData | observed")
            if (it != null) {
                Timber.d("initLiveData | goalsLiveData | observed | goal=%s", it.id)
                onGoalUpdated(it)
            }
        })
        viewModel.fetchGoal()

        viewModel.workoutsLiveData.observe(this, Observer {
            Timber.i("workoutsLiveData | observed")
            if (it != null) {
                Timber.d("workoutsLiveData | observed | workouts=%s", it.size)
                handleWorkoutsUpdate(it)
            }
        })
        viewModel.fetchWorkouts()
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = GoalDetailsAdapter()
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
    private fun onGoalUpdated(runningGoal: RunningGoal) {
        Timber.i("onGoalUpdated")

        handleToolbarUpdate(runningGoal)

        handleListUpdate(runningGoal)

        // Data is loaded so lets wait for our parent to be drawn
        (view?.parent as? ViewGroup)?.doOnPreDraw { _ ->
            // Parent has been drawn. Start transitioning!
            startPostponedEnterTransition()
        }
    }

    private fun handleToolbarUpdate(runningGoal: RunningGoal) {
        val ctx = context
        if (ctx != null) {
            goal_item_name.text = runningGoal.name

            goal_item_status_label.text =
                    when (runningGoal.progress.status) {
                        GoalStatus.ONGOING -> ctx.getString(R.string.status_ongoing)
                        GoalStatus.NOT_STARTED -> ctx.getString(R.string.status_not_started)
                        GoalStatus.EXPIRED -> ctx.getString(R.string.status_expired)
                        GoalStatus.UNKNOWN -> ctx.getString(R.string.status_unknown)
                    }

            goal_item_status_background.imageTintList =
                    when (runningGoal.progress.status) {
                        GoalStatus.ONGOING -> ctx.getColorStateList(R.color.status_ongoing)
                        GoalStatus.NOT_STARTED -> ctx.getColorStateList(R.color.status_not_started)
                        GoalStatus.EXPIRED -> ctx.getColorStateList(R.color.status_expired)
                        GoalStatus.UNKNOWN -> ctx.getColorStateList(R.color.status_not_started)
                    }


            goal_distance_current.setValues(runningGoal.progress.distanceToday.display(true), getString(R.string.current_units, runningGoal.progress.distanceToday.units))
            goal_distance_target.setValues(runningGoal.target.distance.display(true), getString(R.string.target_units, runningGoal.target.distance.units))

            goal_item_edit.setOnClickListener { editGoal(runningGoal) }
            goal_item_sync.setOnClickListener { syncGoal(runningGoal) }

            handleBusyIndicator(runningGoal.view.updating)
        }
    }

    private fun handleListUpdate(goal: RunningGoal) {
        Timber.i("updateDetails")
        recyclerAdapter.setGoal(goal)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun handleWorkoutsUpdate(items: List<Workout>) {
        Timber.i("handleWorkoutsUpdate")
        recyclerAdapter.setWorkout(items)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun handleBusyIndicator(busy: Boolean) {
        Timber.i("handleBusyIndicator")
        Timber.d("handleBusyIndicator | busy=%s", busy)
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
