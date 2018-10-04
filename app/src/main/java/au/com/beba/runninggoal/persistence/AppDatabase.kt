package au.com.beba.runninggoal.persistence

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Database(
        entities = [
            RunningGoalEntity::class,
            SyncSourceEntity::class,
            WidgetEntity::class,
            AthleteActivityEntity::class],
        version = 1,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @VisibleForTesting
        private val DATABASE_NAME: String = "goals"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, executor: Executor = Executors.newSingleThreadExecutor()): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = buildDatabase(context.applicationContext, executor)
                    }
                }
            }
            return INSTANCE!!
        }

        /**
         * Build the database. Builder.build only sets up the database configuration and
         * creates a new instance of the database.
         * The SQLite database is only created when it's accessed for the first time.
         */
        private fun buildDatabase(appContext: Context, executor: Executor): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            executor.execute {
                                // Generate the data for pre-population
                                //AppDatabase.getInstance(appContext, executor)

                                populateSyncSources()

//                                // notify that the database was created and it's ready to be used
//                                //database.setDatabaseCreated()
                            }
                        }
                    })
                    .build()
        }

        private fun populateSyncSources() {
            INSTANCE!!.syncSourceDao().insert(
                    SyncSourceEntity(0,
                            "STRAVA",
                            "",
                            false,
                            LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC))
            )
        }
    }

    abstract fun runningGoalDao(): RunningGoalDao
    abstract fun syncSourceDao(): SyncSourceDao
    abstract fun widgetDao(): WidgetDao
    abstract fun athleteActivityDao(): AthleteActivityDao
}