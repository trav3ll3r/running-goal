package au.com.beba.runninggoal.repo.goal

import android.content.Context
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.goaldatabase.GoalStorage
import au.com.beba.runninggoal.goaldatabase.goal.GoalStorageImpl
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext


class GoalRepo constructor(
        private val context: Context,
        private val coroutineContext: CoroutineContext = DefaultDispatcher)
    : GoalRepository {

    companion object {
        private val TAG = GoalRepo::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val goalStorage: GoalStorage by lazy {
        GoalStorageImpl(context)
    }

    override suspend fun fetchGoals(): List<RunningGoal> = withContext(coroutineContext) {
        logger.info("fetchGoals")
        goalStorage.all()
    }

    override suspend fun getById(goalId: Long): RunningGoal? = withContext(coroutineContext) {
        logger.info("getById")
        goalStorage.getById(goalId)
    }

    override suspend fun save(goal: RunningGoal): Long = withContext(coroutineContext) {
        logger.info("save")
        goalStorage.save(goal)
    }

    override suspend fun delete(goal: RunningGoal): Int = withContext(coroutineContext) {
        logger.info("delete")
        goalStorage.delete(goal)
    }
}