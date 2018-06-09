package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.support.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Database(entities = [(RunningGoalEntity::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @VisibleForTesting
        private val DATABASE_NAME: String = "goals"

        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context, executor: Executor = Executors.newSingleThreadExecutor()): AppDatabase {
            if (sInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = buildDatabase(context.applicationContext, executor)
                    }
                }
            }
            return sInstance!!
        }

        /**
         * Build the database. Builder.build only sets up the database configuration and
         * creates a new instance of the database.
         * The SQLite database is only created when it's accessed for the first time.
         */
        private fun buildDatabase(appContext: Context, executor: Executor): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
//                    .addCallback(object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            executor.execute({
//                                // Generate the data for pre-population
//                                AppDatabase.getInstance(appContext, executor)
//
//                                // notify that the database was created and it's ready to be used
//                                //database.setDatabaseCreated()
//                            })
//                        }
//                    })
                    .allowMainThreadQueries() // FIXME: JUST NO!!!
                    .build()
        }
    }

    abstract fun runningGoalDao(): RunningGoalDao


}

