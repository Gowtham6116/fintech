package com.example.ai

import com.example.BuildConfig
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
  private val client = OkHttpClient.Builder()
    .connectTimeout(25, TimeUnit.SECONDS)
    .readTimeout(25, TimeUnit.SECONDS)
    .build()

  private const val MODEL_NAME = "gemini-3.5-flash"

  suspend fun askFinancialAssistant(
    userPrompt: String,
    userTransactions: List<TransactionEntity>,
    totalIncome: Double,
    totalExpense: Double
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }

    // Aggregate user financial statistics
    val highestCategory = userTransactions
      .filter { it.type == "EXPENSE" }
      .groupBy { it.category }
      .mapValues { entry -> entry.value.sumOf { it.amount } }
      .maxByOrNull { it.value }

    val recentSpendingSummary = if (highestCategory != null) {
      "User spending: Total Income = ₹$totalIncome, Total Expenses = ₹$totalExpense. Top expense category is ${highestCategory.key} (₹${highestCategory.value})."
    } else {
      "User has no recorded transactions yet. Balance = ₹0."
    }

    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
        
        val systemPrompt = """
          You are FinGuide AI, a helpful, encouraging financial assistant for students, first-time earners, and young professionals.
          Context: $recentSpendingSummary
          Important rules:
          1. Provide clear, empathetic, practical budgeting advice and financial education.
          2. Never promise or guarantee specific investment returns.
          3. Never give personalized regulated stock/crypto trading advice; direct users to SEBI-registered advisers.
          4. Keep responses crisp (under 120 words), well-structured with bullet points where appropriate.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
          val contentsArray = JSONArray()
          val contentObj = JSONObject().apply {
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", "$systemPrompt\n\nUser Question: $userPrompt"))
            put("parts", partsArray)
          }
          contentsArray.put(contentObj)
          put("contents", contentsArray)
        }

        val request = Request.Builder()
          .url(url)
          .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
          .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
          val respBody = response.body?.string() ?: ""
          val jsonResponse = JSONObject(respBody)
          val candidates = jsonResponse.optJSONArray("candidates")
          if (candidates != null && candidates.length() > 0) {
            val text = candidates.getJSONObject(0)
              .getJSONObject("content")
              .getJSONArray("parts")
              .getJSONObject(0)
              .getString("text")
            return@withContext text.trim()
          }
        }
      } catch (e: Exception) {
        // Fall back gracefully to deterministic rule-based logic
      }
    }

    // Deterministic Rule-Based Fallback
    getDeterministicResponse(userPrompt, totalIncome, totalExpense, highestCategory?.key, highestCategory?.value)
  }

  private fun getDeterministicResponse(
    prompt: String,
    income: Double,
    expense: Double,
    topCat: String?,
    topCatAmt: Double?
  ): String {
    val q = prompt.lowercase()
    return when {
      q.contains("increase") || q.contains("more") || q.contains("why") -> {
        if (topCat != null) {
          "Based on your recorded ledger, your highest spending category is **$topCat** (₹${topCatAmt?.toInt()}). Check your recent transactions in this category to identify discretionary subscriptions or frequent small payments that compound."
        } else {
          "You haven't recorded significant expenses yet. Add your daily expenses to track whether recurring bills or weekend dining are driving any increases."
        }
      }
      q.contains("reduce") || q.contains("cut") || q.contains("save more") -> {
        "Practical 3-step strategy to reduce spending:\n" +
        "1. **Audit Subscriptions**: Pause unused streaming or gym services.\n" +
        "2. **The 50/30/20 Rule**: Cap needs at 50% of income, allocate 30% to wants, and reserve 20% for savings.\n" +
        "3. **The 24-Hour Pause**: Wait one day before non-essential purchases above ₹1,000."
      }
      q.contains("how much did i spend") || q.contains("total spend") -> {
        "Your recorded total expenses stand at **₹${expense.toInt()}** against an income of **₹${income.toInt()}**. Your net balance is **₹${(income - expense).toInt()}**."
      }
      q.contains("highest") || q.contains("category") -> {
        if (topCat != null) {
          "Your highest expense category is **$topCat** at ₹${topCatAmt?.toInt()}, representing ${if (expense > 0) ((topCatAmt ?: 0.0) / expense * 100).toInt() else 0}% of your total expenses."
        } else {
          "No expenses recorded yet. Start by tapping '+ Add Expense' or using Voice Expense."
        }
      }
      q.contains("compound interest") -> {
        "**Compound Interest** is interest earned on both your initial principal and accumulated interest over time.\n\n" +
        "Formula: A = P(1 + r/n)^(nt)\n" +
        "Example: Investing ₹5,000 monthly in an index fund averaging 12% annual return can grow to over ₹50 Lakhs in 20 years, where compound interest generates the majority of your total wealth!"
      }
      q.contains("emergency fund") -> {
        "**Emergency Fund Guide**:\n" +
        "• **Goal**: Keep 3 to 6 months of essential living expenses (rent, food, bills).\n" +
        "• **Where to keep it**: Liquid Mutual Funds or a High-Yield Savings / Auto-sweep Fixed Deposit (FD) for instant access.\n" +
        "• **Rule**: Never invest emergency money in stocks or volatile assets."
      }
      else -> {
        "Hello! I am your FinGuide Financial Assistant. I can help analyze your spending patterns, explain financial concepts (like compound interest, SIPs, emergency funds), and suggest budgeting tips. Try asking: *'How can I reduce my expenses?'* or *'Explain compound interest'*"
      }
    }
  }
}
