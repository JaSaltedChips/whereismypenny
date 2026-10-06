package app.harsha.wheresmypenny.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.harsha.wheresmypenny.data.db.dao.AccountDao
import app.harsha.wheresmypenny.data.db.dao.CategoryDao
import app.harsha.wheresmypenny.data.db.dao.SettingDao
import app.harsha.wheresmypenny.data.db.dao.TransactionDao
import app.harsha.wheresmypenny.data.db.entity.AccountEntity
import app.harsha.wheresmypenny.data.db.entity.CategoryEntity
import app.harsha.wheresmypenny.data.db.entity.SettingEntity
import app.harsha.wheresmypenny.data.db.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        SettingEntity::class,
    ],
    version = 1,
    exportSchema = true,  // schema JSON committed to app/schemas/ for migration verification
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "wheresmypenny.db",
            )
            // PRODUCTION RULE: never add fallbackToDestructiveMigration() here.
            // Add explicit Migration objects for every schema change before release.
            .build()
    }
}
