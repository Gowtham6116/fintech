package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
  entities = [
    User::class,
    TransactionEntity::class,
    BudgetEntity::class,
    SavingsGoalEntity::class,
    ResourceEntity::class,
    DocumentEntity::class,
    ArticleEntity::class,
    NotificationEntity::class,
    ResourceReportEntity::class,
    ChatMessageEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun transactionDao(): TransactionDao
  abstract fun budgetDao(): BudgetDao
  abstract fun savingsDao(): SavingsDao
  abstract fun resourceDao(): ResourceDao
  abstract fun documentDao(): DocumentDao
  abstract fun articleDao(): ArticleDao
  abstract fun notificationDao(): NotificationDao
  abstract fun reportDao(): ReportDao
  abstract fun chatDao(): ChatDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "finguide_database"
        )
        .fallbackToDestructiveMigration(true)
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
