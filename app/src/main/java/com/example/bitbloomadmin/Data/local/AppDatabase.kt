package com.example.bitbloomadmin.Data.local


import android.content.Context
import androidx.room.*
import com.bitbloom.bitbloomadmin.Dao.UserDao
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.UserModel
import com.bitbloom.bitbloomadmin.utils.TimestampConverter


@Database(
    entities = [UserModel::class, AccountModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "bitbloom_admin_db"
                            ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
