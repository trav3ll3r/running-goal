package au.com.beba.runninggoal.goaldatabase.goal

import android.content.Context
import au.com.beba.runninggoal.domain.Distance
import au.com.beba.runninggoal.domain.GoalDate
import au.com.beba.runninggoal.domain.GoalTarget
import au.com.beba.runninggoal.domain.Period
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.goaldatabase.AppDatabase
import au.com.beba.runninggoal.goaldatabase.GoalStorage
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.coroutineContext


class GoalStorageImpl(private val context: Context)
    : GoalStorage {

    companion object {
        private val TAG = GoalStorageImpl::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private val goalDao: RunningGoalDao by lazy {
        AppDatabase.getInstance(context).runningGoalDao()
    }

    override suspend fun all(): List<RunningGoal> = withContext(coroutineContext) {
        logger.info("fetchGoals")
        val goalEntities = goalDao.getAll()
        logger.debug("fetchGoals | goalEntities=%s".format(goalEntities.size))

        val goals = goalEntities.asSequence().map {
            val goal = RunningGoal(
                    it.uid,
                    it.goalName,
                    GoalTarget(
                            Distance(it.targetDistance),
                            buildUtcPeriod(it)
                    )
            )
            goal.progress.distanceToday = Distance(it.currentDistance)
            goal.updateProgressValues()

            goal
        }.toList()

        logger.debug("fetchGoals | goals=%s".format(goals.size))
        goals
    }

    override suspend fun getById(goalId: Long): RunningGoal? = withContext(coroutineContext) {
        val goalEntity = goalDao.getById(goalId)

        val goal: RunningGoal?
        if (goalEntity != null) {
            goal = RunningGoal(
                    goalEntity.uid,
                    goalEntity.goalName,
                    GoalTarget(
                            Distance(goalEntity.targetDistance),
                            buildUtcPeriod(goalEntity)
                    )
            )

            goal.progress.distanceToday = Distance(goalEntity.currentDistance)
            goal.updateProgressValues()
        } else {
            logger.error("Goal for goalId=%s not found!".format(goalId))
            goal = null
        }

        goal
    }

    override suspend fun save(goal: RunningGoal): Long = withContext(coroutineContext) {
        logger.info("save")
        logger.debug("save | mapFrom | id=%s, distance=%s".format(goal.id, goal.progress.distanceToday.value))

        val goalEntity = RunningGoalEntity(goal.id)
        goalEntity.goalName = goal.name
        goalEntity.targetDistance = goal.target.distance.value
        goalEntity.currentDistance = goal.progress.distanceToday.value

        goalEntity.startDate = goal.target.period.from.asEpochUtc()
        goalEntity.endDate = goal.target.period.to.asEpochUtc()

        var id: Long = goalDao.insert(goalEntity)
        if (id < 0L) {
            logger.info("save | update")
            logger.debug("save | update | uid=%s, distance=%s".format(goalEntity.uid, goalEntity.currentDistance))
            goalDao.update(goalEntity)
            id = goalEntity.uid
        }

        goal.updateProgressValues()

        id
    }

    override suspend fun delete(goal: RunningGoal): Int = withContext(coroutineContext) {
        val goalEntity = RunningGoalEntity(goal.id)
        val deletedRows = goalDao.delete(goalEntity)

        deletedRows
    }

    private fun buildUtcPeriod(goalEntity: RunningGoalEntity): Period {
        return Period(GoalDate(goalEntity.startDate), GoalDate(goalEntity.endDate))
    }
}