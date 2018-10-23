package au.com.beba.runninggoal.feature.goal

import android.content.Context
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import kotlinx.coroutines.experimental.runBlocking

object GoalFeatureImpl : GoalFeature {

    override val isSuspended = false
    override val isReady = true
    private lateinit var goalRepo: GoalRepository

    override fun bootstrap(application: Context) {
        goalRepo = GoalRepo(application)
    }

    override fun fetchGoals(): List<RunningGoal> {
        return runBlocking { goalRepo.fetchGoals() }
    }

    override fun getById(goalId: Long): RunningGoal? {
        return runBlocking { goalRepo.getById(goalId) }
    }

    override fun save(goal: RunningGoal): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(goal: RunningGoal): Int {
        return runBlocking { goalRepo.delete(goal) }
    }
}