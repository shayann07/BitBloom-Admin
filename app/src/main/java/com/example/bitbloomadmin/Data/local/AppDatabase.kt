package com.example.bitbloomadmin.Data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bitbloom.bitbloomadmin.utils.TimestampConverter
import com.example.bitbloomadmin.Dao.UserDao
import com.example.bitbloomadmin.Dao.UserPlanDao
import com.example.bitbloomadmin.Dao.WithdrawDao
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.UserModel
import com.example.bitbloomadmin.models.UserPlanEntity
import com.example.bitbloomadmin.models.WithdrawModel
import com.example.bitbloomadmin.models.WithdrawWithUserName
import com.example.bitbloomadmin.utils.RoomConverters

@Database(
    entities = [UserModel::class, AccountModel::class, WithdrawModel::class, WithdrawWithUserName::class, UserPlanEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class, RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun withdrawDao(): WithdrawDao
    abstract fun userPlanDao(): UserPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "bitbloom_admin_db"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
