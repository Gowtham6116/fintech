package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): User?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserById(id: String): User?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<User>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: User)

  @Update
  suspend fun updateUser(user: User)

  @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
  suspend fun setUserActiveStatus(userId: String, isActive: Boolean)

  @Query("DELETE FROM users WHERE id = :userId")
  suspend fun deleteUser(userId: String)
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, createdAt DESC")
  fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY date DESC")
  fun getTransactionsByType(userId: String, type: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getTransactionById(id: String): TransactionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity)

  @Update
  suspend fun updateTransaction(transaction: TransactionEntity)

  @Query("DELETE FROM transactions WHERE id = :id AND userId = :userId")
  suspend fun deleteTransaction(id: String, userId: String)

  @Query("DELETE FROM transactions WHERE userId = :userId")
  suspend fun deleteAllForUser(userId: String)
}

@Dao
interface BudgetDao {
  @Query("SELECT * FROM budgets WHERE userId = :userId")
  fun getBudgetsForUser(userId: String): Flow<List<BudgetEntity>>

  @Query("SELECT * FROM budgets WHERE userId = :userId AND category = :category LIMIT 1")
  suspend fun getBudgetByCategory(userId: String, category: String): BudgetEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBudget(budget: BudgetEntity)

  @Update
  suspend fun updateBudget(budget: BudgetEntity)

  @Query("DELETE FROM budgets WHERE id = :id AND userId = :userId")
  suspend fun deleteBudget(id: String, userId: String)
}

@Dao
interface SavingsDao {
  @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdAt DESC")
  fun getGoalsForUser(userId: String): Flow<List<SavingsGoalEntity>>

  @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
  suspend fun getGoalById(id: String): SavingsGoalEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoal(goal: SavingsGoalEntity)

  @Update
  suspend fun updateGoal(goal: SavingsGoalEntity)

  @Query("DELETE FROM savings_goals WHERE id = :id AND userId = :userId")
  suspend fun deleteGoal(id: String, userId: String)
}

@Dao
interface ResourceDao {
  @Query("SELECT * FROM resources WHERE isArchived = 0 ORDER BY name ASC")
  fun getAllActiveResources(): Flow<List<ResourceEntity>>

  @Query("SELECT * FROM resources ORDER BY createdAt DESC")
  fun getAllResourcesAdmin(): Flow<List<ResourceEntity>>

  @Query("SELECT * FROM resources WHERE id = :id LIMIT 1")
  suspend fun getResourceById(id: String): ResourceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResource(resource: ResourceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(resources: List<ResourceEntity>)

  @Update
  suspend fun updateResource(resource: ResourceEntity)

  @Query("UPDATE resources SET verificationStatus = :status, lastVerifiedAt = :date WHERE id = :id")
  suspend fun updateVerification(id: String, status: String, date: String)

  @Query("DELETE FROM resources WHERE id = :id")
  suspend fun deleteResource(id: String)
}

@Dao
interface DocumentDao {
  @Query("SELECT * FROM documents WHERE userId = :userId ORDER BY uploadedAt DESC")
  fun getDocumentsForUser(userId: String): Flow<List<DocumentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDocument(doc: DocumentEntity)

  @Query("DELETE FROM documents WHERE id = :id AND userId = :userId")
  suspend fun deleteDocument(id: String, userId: String)
}

@Dao
interface ArticleDao {
  @Query("SELECT * FROM articles WHERE status = 'PUBLISHED' ORDER BY createdAt DESC")
  fun getPublishedArticles(): Flow<List<ArticleEntity>>

  @Query("SELECT * FROM articles ORDER BY createdAt DESC")
  fun getAllArticlesAdmin(): Flow<List<ArticleEntity>>

  @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
  suspend fun getArticleById(id: String): ArticleEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertArticle(article: ArticleEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(articles: List<ArticleEntity>)

  @Update
  suspend fun updateArticle(article: ArticleEntity)

  @Query("DELETE FROM articles WHERE id = :id")
  suspend fun deleteArticle(id: String)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 'ALL' ORDER BY createdAt DESC")
  fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity)

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: String)

  @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR userId = 'ALL'")
  suspend fun markAllAsRead(userId: String)
}

@Dao
interface ReportDao {
  @Query("SELECT * FROM resource_reports ORDER BY createdAt DESC")
  fun getAllReports(): Flow<List<ResourceReportEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReport(report: ResourceReportEntity)

  @Query("UPDATE resource_reports SET status = :status, adminNote = :note WHERE id = :id")
  suspend fun updateReportStatus(id: String, status: String, note: String)
}

@Dao
interface ChatDao {
  @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
  fun getChatHistory(userId: String): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity)

  @Query("DELETE FROM chat_messages WHERE userId = :userId")
  suspend fun clearChatHistory(userId: String)
}
