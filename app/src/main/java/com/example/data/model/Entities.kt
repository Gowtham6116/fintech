package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
  USER,
  ADMIN
}

enum class UserType(val label: String) {
  STUDENT("Student"),
  FIRST_TIME_EARNER("First-time Earner"),
  WORKING_PROFESSIONAL("Working Professional"),
  BEGINNER("Beginner")
}

@Entity(tableName = "users")
data class User(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val passwordHash: String,
  val address: String = "",
  val city: String = "",
  val state: String = "",
  val postalCode: String = "",
  val role: String = "USER", // "USER" or "ADMIN"
  val userType: String = "STUDENT",
  val preferredLanguage: String = "en",
  val currency: String = "INR",
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  val residentialAddress: String get() = address
}

enum class TransactionType {
  INCOME,
  EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val type: String, // "INCOME" or "EXPENSE"
  val amount: Double,
  val category: String, // Food, Transport, Shopping, Education, Bills, Healthcare, Entertainment, Other, or Salary, Freelance, etc.
  val description: String,
  val date: String, // YYYY-MM-DD
  val paymentMode: String, // Cash, UPI, Card, Bank Transfer, Other
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "budgets")
data class BudgetEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val category: String,
  val monthlyLimit: Double,
  val monthYear: String, // "YYYY-MM"
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val title: String,
  val targetAmount: Double,
  val currentAmount: Double,
  val deadline: String,
  val category: String = "General",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "resources")
data class ResourceEntity(
  @PrimaryKey val id: String,
  val name: String,
  val category: String, // Banks, Investment Advisers, Registered Valuers, Jewellers, Insurance, Land Registration Offices, Property Services
  val address: String,
  val city: String,
  val state: String,
  val phone: String,
  val latitude: Double,
  val longitude: Double,
  val authority: String, // SEBI, IBBI, BIS, TNREGINET, RBI
  val registrationNumber: String,
  val verificationStatus: String, // "VERIFIED", "UNVERIFIED", "NEEDS_RECHECK"
  val sourceUrl: String,
  val lastVerifiedAt: String,
  val remarks: String = "",
  val isArchived: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "documents")
data class DocumentEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val fileName: String,
  val fileType: String,
  val category: String, // Insurance, Property, Banking, Tax, Investment, Other
  val fileSizeFormatted: String,
  val storageUri: String,
  val uploadDate: String = "",
  val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "articles")
data class ArticleEntity(
  @PrimaryKey val id: String,
  val title: String,
  val category: String, // Budgeting, Saving, Emergency Fund, Banking, Loans, Interest, Inflation, Mutual Funds, SIP, FD, Gold, Government Bonds, Insurance, Financial Safety
  val summary: String,
  val explanation: String,
  val example: String,
  val keyPointsJson: String, // JSON or pipe-delimited points
  val sourceAuthority: String,
  val sourceUrl: String,
  val language: String = "en",
  val status: String = "PUBLISHED",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String,
  val userId: String, // specific userId or "ALL"
  val title: String,
  val message: String,
  val type: String, // "BUDGET_ALERT", "SAVINGS_REMINDER", "RESOURCE_UPDATE", "SYSTEM"
  val targetAudience: String = "ALL", // ALL, STUDENTS, FIRST_TIME_EARNERS, WORKING_PROFESSIONALS
  val isRead: Boolean = false,
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "resource_reports")
data class ResourceReportEntity(
  @PrimaryKey val id: String,
  val resourceId: String,
  val resourceName: String,
  val userId: String,
  val userEmail: String,
  val reason: String,
  val description: String,
  val status: String = "PENDING", // "PENDING", "RESOLVED", "REJECTED"
  val adminNote: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val sender: String, // "USER" or "AI"
  val message: String,
  val timestamp: Long = System.currentTimeMillis()
)
