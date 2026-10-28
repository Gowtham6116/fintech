package com.example.voice

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

data class ParsedExpense(
  val amount: Double,
  val category: String,
  val description: String,
  val date: String,
  val paymentMode: String = "UPI"
)

object VoiceExpenseParser {
  private val amountPattern = Pattern.compile(
    "(?i)(?:rs\\.?|rupees?|inr|₹|bucks?|ரூபாய்|ரூ\\.?|ரூ|रुपये|रुपया|రూపాయలు|రూ\\.?|ರೂಪಾಯಿ|ರೂ\\.?|രൂപ|টাকা)?\\s*(\\d+(?:[.,]\\d{1,2})?)\\s*(?:rs\\.?|rupees?|inr|₹|ரூபாய்|ரூ\\.?|ரூ|रुपये|रुपया|రూపాయలు|రూ\\.?|ರೂಪಾಯಿ|ರೂ\\.?|രൂപ|টাকা|/-)?"
  )

  private val wordNumbers = mapOf(
    "one" to 1.0, "two" to 2.0, "three" to 3.0, "four" to 4.0, "five" to 5.0,
    "six" to 6.0, "seven" to 7.0, "eight" to 8.0, "nine" to 9.0, "ten" to 10.0,
    "fifteen" to 15.0, "twenty" to 20.0, "thirty" to 30.0, "forty" to 40.0,
    "fifty" to 50.0, "sixty" to 60.0, "seventy" to 70.0, "eighty" to 80.0,
    "ninety" to 90.0, "hundred" to 100.0, "two hundred" to 200.0, "two fifty" to 250.0, "three hundred" to 300.0,
    "four hundred" to 400.0, "five hundred" to 500.0, "thousand" to 1000.0,
    "one thousand" to 1000.0, "two thousand" to 2000.0, "five thousand" to 5000.0,
    // Tamil numbers
    "பத்து" to 10.0, "இருபது" to 20.0, "முப்பது" to 30.0, "நாற்பது" to 40.0,
    "ஐம்பது" to 50.0, "அறுபது" to 60.0, "எழுபது" to 70.0, "எண்பது" to 80.0, "தொண்ணூறு" to 90.0,
    "நூறு" to 100.0, "இருநூறு" to 200.0, "இருநூற்று ஐம்பது" to 250.0, "முந்நூறு" to 300.0, "நானூறு" to 400.0,
    "ஐந்நூறு" to 500.0, "ஆயிரம்" to 1000.0, "இரண்டாயிரம்" to 2000.0, "ஐந்தாயிரம்" to 5000.0,
    // Hindi numbers
    "दस" to 10.0, "बीस" to 20.0, "तीस" to 30.0, "चालीस" to 40.0, "पचास" to 50.0,
    "साठ" to 60.0, "सत्तर" to 70.0, "अस्सी" to 80.0, "नब्बे" to 90.0,
    "सौ" to 100.0, "दो सौ" to 200.0, "ढाई सौ" to 250.0, "तीन सौ" to 300.0, "चार सौ" to 400.0,
    "पाँच सौ" to 500.0, "पांच सौ" to 500.0, "हज़ार" to 1000.0, "हजार" to 1000.0, "दो हजार" to 2000.0,
    // Telugu numbers
    "పది" to 10.0, "ఇరవై" to 20.0, "యాభై" to 50.0, "వంద" to 100.0, "రెండు వందలు" to 200.0, "ఐదు వందలు" to 500.0, "వెయ్యి" to 1000.0,
    // Kannada numbers
    "ಹತ್ತು" to 10.0, "ಇಪ್ಪತ್ತು" to 20.0, "ಐವತ್ತು" to 50.0, "ನೂರು" to 100.0, "ಐನೂರು" to 500.0, "ಸಾವಿರ" to 1000.0,
    // Malayalam numbers
    "പത്ത്" to 10.0, "ഇരുപത്" to 20.0, "അമ്പത്" to 50.0, "നൂറ്" to 100.0, "അഞ്ഞൂറ്" to 500.0, "ആയിരം" to 1000.0
  )

  private val categoryKeywords = mapOf(
    "Food" to listOf(
      "lunch", "dinner", "breakfast", "tea", "coffee", "food", "snack", "swiggy", "zomato",
      "restaurant", "burger", "pizza", "biryani", "meals", "groceries", "supermarket", "vegetables", "milk", "fruits", "juice",
      "உணவு", "மதிய உணவு", "இரவு உணவு", "காலை உணவு", "சாப்பாடு", "தேநீர்", "காபி", "சிற்றுண்டி", "மளிகை",
      "खाना", "लंच", "डिनर", "नाश्ता", "चाय", "कॉफ़ी", "सब्जी", "दूध", "राशन", "होटल"
    ),
    "Transport" to listOf(
      "uber", "ola", "auto", "metro", "bus", "train", "fuel", "petrol", "diesel", "cab", "taxi", "fare", "travel", "flight", "ticket", "toll",
      "பேருந்து", "பஸ்", "ரயில்", "ஆட்டோ", "பெட்ரோல்", "டீசல்", "பயணம்", "டிக்கெட்",
      "बस", "ट्रेन", "ऑटो", "पेट्रोल", "डीजल", "सफ़र", "किराया", "मेट्रो"
    ),
    "Shopping" to listOf(
      "clothes", "shirt", "shoes", "shopping", "amazon", "flipkart", "myntra", "watch", "dress", "jeans", "mall",
      "துணி", "ஆடை", "செருப்பு", "ஷாப்பிங்", "அமேசான்",
      "कपड़े", "जूते", "शॉपिंग", "अमेज़न", "फ्लिपकार्ट"
    ),
    "Education" to listOf(
      "book", "course", "tuition", "exam", "fees", "stationery", "udemy", "college", "school", "pencil", "pen",
      "புத்தகம்", "படிப்பு", "கட்டணம்", "பள்ளி", "கல்லூரி", "நோட்டு",
      "किताब", "फीस", "ट्यूशन", "कॉलेज", "स्कूल", "पढ़ाई"
    ),
    "Bills" to listOf(
      "recharge", "electricity", "wifi", "broadband", "rent", "water", "bill", "postpaid", "gas", "cylinder", "maintenance", "eb", "current",
      "ரீசார்ஜ்", "மின்சாரம்", "வாடகை", "தண்ணீர்", "பில்", "எரிவாயு",
      "रिचार्ज", "बिजली", "किराया", "पानी", "बिल", "गैस"
    ),
    "Healthcare" to listOf(
      "medicine", "doctor", "pharmacy", "hospital", "clinic", "tablets", "medical", "checkup", "dentist", "injection",
      "மருந்து", "மருத்துவர்", "மருத்துவமனை", "மாத்திரை",
      "दवा", "डॉक्टर", "अस्पताल", "गोली", "इलाज"
    ),
    "Entertainment" to listOf(
      "movie", "cinema", "netflix", "hotstar", "spotify", "concert", "game", "outing", "party", "theater",
      "திரைப்படம்", "சினிமா", "தியேட்டர்", "விளையாட்டு",
      "फिल्म", "सिनेमा", "मूवी", "पार्टी"
    )
  )

  private val paymentKeywords = mapOf(
    "UPI" to listOf("upi", "gpay", "phonepe", "paytm", "scan", "qr", "online", "கூகுள் பே", "போன்பே", "पेटीएम"),
    "Cash" to listOf("cash", "hand", "ரொக்கம்", "பணம்", "நकद", "कैश"),
    "Card" to listOf("card", "debit", "credit", "pos", "கார்டு", "கார்ட்", "कार्ड"),
    "Bank Transfer" to listOf("netbanking", "transfer", "neft", "imps", "bank", "வங்கி", "बैंक")
  )

  fun parseSpokenText(spokenText: String): ParsedExpense? {
    if (spokenText.isBlank()) return null

    val lower = spokenText.lowercase().trim()

    // 1. Extract Amount from digits or words
    var amount = 0.0

    // Check regex pattern first
    val matcher = amountPattern.matcher(lower)
    while (matcher.find()) {
      val parsed = matcher.group(1)?.toDoubleOrNull()
      if (parsed != null && parsed > 0.0) {
        amount = parsed
        break
      }
    }

    // Check general numbers
    if (amount <= 0.0) {
      val digitMatch = Regex("""\b(\d+(?:\.\d{1,2})?)\b""").find(lower)
      if (digitMatch != null) {
        amount = digitMatch.groupValues[1].toDoubleOrNull() ?: 0.0
      }
    }

    // Check word-based numbers (e.g. "two hundred", "fifty")
    if (amount <= 0.0) {
      for ((phrase, value) in wordNumbers) {
        if (lower.contains(phrase)) {
          amount = value
          break
        }
      }
    }

    // 2. Identify Category
    var matchedCategory = "Other"
    for ((category, keywords) in categoryKeywords) {
      if (keywords.any { lower.contains(it) }) {
        matchedCategory = category
        break
      }
    }

    // 3. Payment Mode
    var matchedPayment = "UPI"
    for ((payment, keywords) in paymentKeywords) {
      if (keywords.any { lower.contains(it) }) {
        matchedPayment = payment
        break
      }
    }

    // 4. Clean Description
    var description = spokenText
      .replace(Regex("(?i)\\b(i spent|spent|paid|bought|cost|purchased|for|rupees?|rs\\.?|₹|inr|today|yesterday|ரூபாய்|ரூ\\.?|ரொக்கம்|रुपये|रुपया|दिए|खर्च)\\b"), "")
      .replace(Regex("\\b\\d+(\\.\\d+)?\\b"), "")
      .trim()

    // Clean up punctuation
    description = description.replace(Regex("^[\\s,.-]+|[\\s,.-]+$"), "").trim()

    if (description.isBlank() || description.length < 2) {
      description = when (matchedCategory) {
        "Food" -> "Food & Dining"
        "Transport" -> "Travel & Commute"
        "Bills" -> "Utility Bill"
        "Shopping" -> "Shopping"
        "Education" -> "Books & Study"
        "Healthcare" -> "Medical & Pharmacy"
        "Entertainment" -> "Entertainment"
        else -> spokenText.take(30).trim()
      }
    }

    // 5. Date
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateStr = sdf.format(Date())

    return ParsedExpense(
      amount = if (amount > 0) amount else 0.0,
      category = matchedCategory,
      description = description.take(40).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
      date = dateStr,
      paymentMode = matchedPayment
    )
  }
}
