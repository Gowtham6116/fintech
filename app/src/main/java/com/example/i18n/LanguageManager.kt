package com.example.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalAppLanguage = compositionLocalOf { "en" }

@Composable
fun tr(textOrKey: String?): String {
  val lang = LocalAppLanguage.current
  return LanguageManager.translate(textOrKey, lang)
}

@Composable
@JvmName("trExtension")
fun String.tr(): String {
  val lang = LocalAppLanguage.current
  return LanguageManager.translate(this, lang)
}

data class LanguageInfo(
  val code: String,
  val englishName: String,
  val nativeName: String
)

object LanguageManager {
  val supportedLanguages = listOf(
    LanguageInfo("en", "English", "English"),
    LanguageInfo("ta", "Tamil", "தமிழ்"),
    LanguageInfo("hi", "Hindi", "हिन्दी"),
    LanguageInfo("te", "Telugu", "తెలుగు"),
    LanguageInfo("kn", "Kannada", "ಕನ್ನಡ"),
    LanguageInfo("ml", "Malayalam", "മലയാളം"),
    LanguageInfo("bn", "Bengali", "বাংলা"),
    LanguageInfo("mr", "Marathi", "मराठी"),
    LanguageInfo("gu", "Gujarati", "ગુજરાતી"),
    LanguageInfo("pa", "Punjabi", "ਪੰਜਾਬੀ"),
    LanguageInfo("or", "Odia", "ଓଡ଼ିଆ"),
    LanguageInfo("as", "Assamese", "অসমীয়া"),
    LanguageInfo("ur", "Urdu", "اردو"),
    LanguageInfo("ne", "Nepali", "नेपाली"),
    LanguageInfo("kok", "Konkani", "कोंकणी")
  )

  private val enStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "Manage Money. Discover Resources. Build a Better Future.",
    "nav_home" to "Home",
    "nav_finance" to "Finance",
    "nav_learn" to "Learn",
    "nav_resources" to "Resources",
    "nav_profile" to "Profile",
    "nav_documents" to "Documents",
    "nav_notifications" to "Alerts",
    "nav_admin" to "Admin",
    "nav_settings" to "Settings",
    "nav_budget" to "Budgets",
    "nav_ai" to "AI Chat",

    "save" to "Save",
    "cancel" to "Cancel",
    "edit" to "Edit",
    "delete" to "Delete",
    "confirm" to "Confirm",
    "back" to "Back",
    "close" to "Close",
    "search" to "Search",
    "filter" to "Filter",
    "upload" to "Upload",
    "download" to "Download",
    "view" to "View",
    "retry" to "Retry",
    "submit" to "Submit",
    "clear" to "Clear",
    "next" to "Next",
    "get_started" to "Get Started",
    "logout" to "Logout",
    "sign_in" to "Sign In",
    "sign_up" to "Sign Up",
    "create_account" to "Create Account",
    "save_changes" to "Save Changes",
    "view_all" to "View All",
    "details" to "Details",
    "status" to "Status",
    "actions" to "Actions",

    "dashboard" to "Dashboard",
    "greeting_morning" to "Good Morning",
    "greeting_afternoon" to "Good Afternoon",
    "greeting_evening" to "Good Evening",
    "total_balance" to "Total Balance",
    "income" to "Income",
    "expenses" to "Expenses",
    "savings" to "Savings",
    "add_expense" to "Add Expense",
    "add_income" to "Add Income",
    "budget" to "Budget",
    "savings_goal" to "Savings Goal",
    "voice_expense" to "Voice Expense",
    "recent_transactions" to "Recent Transactions",
    "monthly_spending" to "Monthly Spending",
    "quick_actions" to "Quick Actions",
    "no_transactions_yet" to "No transactions recorded yet.",
    "financial_overview" to "Financial Overview",
    "smart_ai_insights" to "AI Financial Insight",

    "transactions_title" to "Income & Expense Ledger",
    "filter_all" to "All",
    "filter_today" to "Today",
    "filter_week" to "This Week",
    "filter_month" to "This Month",
    "amount" to "Amount",
    "category" to "Category",
    "description" to "Description",
    "date" to "Date",
    "payment_mode" to "Payment Mode",
    "save_expense" to "Save Expense",
    "save_income" to "Save Income",
    "edit_transaction" to "Edit Transaction",
    "delete_transaction_confirm" to "Are you sure you want to delete this transaction?",
    "search_placeholder" to "Search transactions...",

    "budget_title" to "Budgets & Savings Goals",
    "monthly_budget" to "Monthly Budget",
    "spent" to "Spent",
    "remaining" to "Remaining",
    "used" to "Used",
    "set_budget" to "Set Category Budget",
    "category_budgets" to "Category Budgets",
    "savings_title" to "Savings Goals",
    "target_amount" to "Target Amount",
    "current_saved" to "Current Saved",
    "deadline" to "Deadline",
    "add_goal" to "Add Goal",
    "deposit" to "Deposit",
    "progress" to "Progress",

    "voice_expense_title" to "Voice Expense Entry",
    "speak_expense" to "Speak your expense naturally",
    "voice_instruction" to "Tap microphone and speak: e.g. 'Spent 250 rupees for lunch via UPI'",
    "listening_tap_stop" to "Listening... Tap to stop recording",
    "tap_to_speak" to "Tap Microphone to Speak",
    "text_fallback_label" to "Recognized Speech Transcript",
    "voice_unavailable" to "Speech recognition is not available on this device.",
    "parse_and_review" to "Parse Expense",
    "enter_manually" to "Enter Expense Manually",
    "did_you_mean" to "Did you mean?",
    "recording_started" to "Listening to speech...",
    "recording_stopped" to "Recording ended. Review parsed candidate.",

    "ai_assistant_title" to "FinGuide AI Assistant",
    "ai_subtitle" to "Personalized guidance & financial queries",
    "ask_ai_placeholder" to "Ask any financial question...",
    "ask_button" to "Ask FinGuide",
    "suggested_questions" to "Suggested Questions",
    "disclaimer_ai" to "AI provides financial literacy suggestions. For official verification consult registered authorities.",

    "learn_title" to "Financial Education Hub",
    "learn_subtitle" to "Verified guides by RBI, SEBI, IBBI & BIS",
    "search_topics" to "Search articles, savings, mutual funds...",
    "categories" to "Categories",
    "read_article" to "Read Article",
    "key_takeaways" to "Key Takeaways",
    "practical_example" to "Practical Example",
    "official_source" to "Official Authority Source",

    "resources_title" to "Nearby Financial Authorities",
    "resources_subtitle" to "Locate verified banks, advisers & land registries",
    "search_resources" to "Search authorities or enter location...",
    "list_view" to "List View",
    "map_view" to "Map View",
    "distance_km" to "km away",
    "verified_badge" to "Verified",
    "unverified_badge" to "Unverified",
    "needs_recheck_badge" to "Needs Recheck",
    "call_now" to "Call",
    "open_map" to "Directions",
    "report_incorrect" to "Report Incorrect Info",
    "official_authority" to "Regulating Authority",

    "documents_title" to "Secure Document Vault",
    "documents_subtitle" to "Private encrypted financial documents",
    "upload_document" to "Upload Document",
    "file_name" to "File Name",
    "file_size" to "File Size",
    "upload_date" to "Upload Date",
    "vault_description" to "Store policies, deeds, receipts securely on device",
    "no_documents" to "No documents uploaded yet.",
    "save_to_vault" to "Save to Vault",
    "select_file" to "Select File (PDF, PNG, JPG)",

    "notifications_title" to "Notifications & Alerts",
    "notifications_subtitle" to "Budget thresholds & official updates",
    "mark_all_read" to "Mark All as Read",
    "no_notifications" to "No notifications right now.",

    "profile_title" to "My Profile & Preferences",
    "edit_profile" to "Edit Profile",
    "full_name" to "Full Name",
    "email_address" to "Email Address",
    "profession_type" to "Profession Type",
    "address_section" to "Residential Address",
    "address_line" to "Street Address / House No.",
    "city" to "City",
    "state" to "State",
    "postal_code" to "Postal Code (PIN)",
    "preferred_language" to "Preferred Language",
    "cloud_sync" to "Firestore Cloud Ledger Sync",
    "sync_to_cloud" to "Sync Room to Cloud",
    "pull_to_room" to "Pull Cloud to Room",
    "profile_updated_success" to "Profile updated successfully!",

    "login_title" to "Welcome Back to FinGuide",
    "login_subtitle" to "Sign in to access your finances and verified authorities",
    "register_title" to "Create Your Account",
    "register_subtitle" to "Register with address & preferred language",
    "email_label" to "Email Address",
    "password_label" to "Password",
    "confirm_password_label" to "Confirm Password",
    "dont_have_account" to "Don't have an account?",
    "already_have_account" to "Already have an account?",
    "admin_portal_access" to "Administrator Portal",
    "user_type_label" to "User Type",
    "student" to "Student",
    "first_time_earner" to "First-time Earner",
    "working_professional" to "Working Professional",
    "address_required_error" to "Address line and city cannot be empty",
    "admin_login_title" to "FinGuide Admin Portal Login",
    "admin_login_subtitle" to "Restricted access for registry administrators",
    "admin_signin_button" to "Admin Secure Login",
    "switch_to_user_login" to "Back to User Login",

    "admin_portal" to "Admin Portal",
    "admin_dashboard" to "Admin Dashboard",
    "admin_users" to "Users Management",
    "admin_resources" to "Resources Registry",
    "admin_verification" to "Verification Desk",
    "admin_education" to "Education Manager",
    "admin_reports" to "User Reports",
    "admin_notifications" to "Broadcast Alerts",
    "admin_settings" to "System Settings",
    "total_users" to "Total Users",
    "active_users" to "Active Users",
    "verified_resources" to "Verified Resources",
    "pending_verification" to "Pending Verification",
    "needs_recheck" to "Needs Recheck",
    "reported_resources" to "Reported Resources"
  )

  private val taStrings = mapOf(
    "app_name" to "ஃபின்கைட் (FinGuide)",
    "tagline" to "பணத்தை நிர்வகியுங்கள். வளங்களைக் கண்டறியுங்கள். சிறந்த எதிர்காலத்தை உருவாக்குங்கள்.",
    "nav_home" to "முகப்பு",
    "nav_finance" to "நிதி",
    "nav_learn" to "கல்வி",
    "nav_resources" to "வளங்கள்",
    "nav_profile" to "சுயவிவரம்",
    "nav_documents" to "ஆவணங்கள்",
    "nav_notifications" to "அறிவிப்புகள்",
    "nav_admin" to "நிர்வாகம்",
    "nav_settings" to "அமைப்புகள்",
    "nav_budget" to "பட்ஜெட்",
    "nav_ai" to "AI உரையாடல்",

    "save" to "சேமிக்கவும்",
    "cancel" to "ரத்துசெய்",
    "edit" to "திருத்து",
    "delete" to "நீக்கு",
    "confirm" to "உறுதிப்படுத்து",
    "back" to "பின்செல்",
    "close" to "மூடு",
    "search" to "தேடு",
    "filter" to "வடிகட்டு",
    "upload" to "பதிவேற்று",
    "download" to "பதிவிறக்கு",
    "view" to "பார்வை",
    "retry" to "மீண்டும் முயற்சி",
    "submit" to "சமர்ப்பி",
    "clear" to "அழி",
    "next" to "அடுத்து",
    "get_started" to "தொடங்குங்கள்",
    "logout" to "வெளியேறு",
    "sign_in" to "உள்நுழை",
    "sign_up" to "பதிவுசெய்க",
    "create_account" to "கணக்கு தொடங்கவும்",
    "save_changes" to "மாற்றங்களைச் சேமிக்கவும்",
    "view_all" to "அனைத்தையும் காண்க",
    "details" to "விவரங்கள்",
    "status" to "நிலை",
    "actions" to "செயல்கள்",

    "dashboard" to "முகப்பு / டாஷ்போர்டு",
    "greeting_morning" to "காலை வணக்கம்",
    "greeting_afternoon" to "மதிய வணக்கம்",
    "greeting_evening" to "மாலை வணக்கம்",
    "total_balance" to "மொத்த இருப்பு",
    "income" to "வருமானம்",
    "expenses" to "செலவுகள்",
    "savings" to "சேமிப்பு",
    "add_expense" to "செலவு சேர்க்கவும்",
    "add_income" to "வருமானம் சேர்க்கவும்",
    "budget" to "பட்ஜெட்",
    "savings_goal" to "சேமிப்பு இலக்கு",
    "voice_expense" to "குரல் மூலம் செலவு",
    "recent_transactions" to "சமீபத்திய பரிவர்த்தனைகள்",
    "monthly_spending" to "மாதாந்திர செலவு",
    "quick_actions" to "விரைவுச் செயல்கள்",
    "no_transactions_yet" to "இன்னும் பரிவர்த்தனைகள் எதுவும் இல்லை.",
    "financial_overview" to "நிதி கண்ணோட்டம்",
    "smart_ai_insights" to "AI நிதி வழிகாட்டல்",

    "transactions_title" to "வருமானம் மற்றும் செலவு கணக்கு",
    "filter_all" to "அனைத்தும்",
    "filter_today" to "இன்று",
    "filter_week" to "இந்த வாரம்",
    "filter_month" to "இந்த மாதம்",
    "amount" to "தொகை",
    "category" to "வகை",
    "description" to "விளக்கம்",
    "date" to "தேதி",
    "payment_mode" to "பணம் செலுத்தும் முறை",
    "save_expense" to "செலவைச் சேமிக்கவும்",
    "save_income" to "வருமானத்தைச் சேமிக்கவும்",
    "edit_transaction" to "பரிவர்த்தனையைத் திருத்து",
    "delete_transaction_confirm" to "இந்தப் பரிவர்த்தனையை நிச்சயமாக நீக்க விரும்புகிறீர்களா?",
    "search_placeholder" to "பரிவர்த்தனைகளைத் தேடவும்...",

    "budget_title" to "பட்ஜெட் மற்றும் சேமிப்பு இலக்குகள்",
    "monthly_budget" to "மாதாந்திர பட்ஜெட்",
    "spent" to "செலவிடப்பட்டது",
    "remaining" to "மீதமுள்ளது",
    "used" to "பயன்படுத்தப்பட்டது",
    "set_budget" to "பட்ஜெட் நிர்ணயிக்கவும்",
    "category_budgets" to "வகை வாரியான பட்ஜெட்",
    "savings_title" to "சேமிப்பு இலக்குகள்",
    "target_amount" to "இலக்கு தொகை",
    "current_saved" to "தற்போதைய சேமிப்பு",
    "deadline" to "கடைசி தேதி",
    "add_goal" to "இலக்கு சேர்க்க",
    "deposit" to "டெபாசிட்",
    "progress" to "முன்னேற்றம்",

    "voice_expense_title" to "குரல் வழி செலவுப் பதிவு",
    "speak_expense" to "உங்கள் செலவை இயல்பாகப் பேசுங்கள்",
    "voice_instruction" to "மைக்ரோஃபோனைத் தட்டிப் பேசுங்கள்: எ.கா. 'மதிய உணவுக்கு 250 ரூபாய் UPI மூலம் செலவு செய்தேன்'",
    "listening_tap_stop" to "கேட்கிறது... பதிவை நிறுத்த தட்டவும்",
    "tap_to_speak" to "பேச மைக்ரோஃபோனைத் தட்டவும்",
    "text_fallback_label" to "அங்கீகரிக்கப்பட்ட உரை விளக்கம்",
    "voice_unavailable" to "இந்தச் சாதனத்தில் குரல் உள்ளீடு கிடைக்கவில்லை.",
    "parse_and_review" to "செலவைப் பிரித்தறி",
    "enter_manually" to "கைமுறையாக உள்ளிடவும்",
    "did_you_mean" to "நீங்கள் இதைச் சொல்கிறீர்களா?",
    "recording_started" to "குரல் பதிவு தொடங்கப்பட்டது...",
    "recording_stopped" to "பதிவு முடிந்தது. விவரங்களைச் சரிபார்க்கவும்.",

    "ai_assistant_title" to "ஃபின்கைட் AI நிதி உதவியாளர்",
    "ai_subtitle" to "தனிப்பயனாக்கப்பட்ட வழிகாட்டல் மற்றும் கேள்விகள்",
    "ask_ai_placeholder" to "நிதி சார்ந்த கேள்விகளைக் கேட்கவும்...",
    "ask_button" to "கேட்கவும்",
    "suggested_questions" to "பரிந்துரைக்கப்பட்ட கேள்விகள்",
    "disclaimer_ai" to "AI தகவலறிவுக்கான வழிகாட்டலை வழங்குகிறது. அதிகாரப்பூர்வ தகவல்களுக்குப் பதிவுபெற்ற அதிகாரிகளை அணுகவும்.",

    "learn_title" to "நிதி விழிப்புணர்வு மையம்",
    "learn_subtitle" to "RBI, SEBI, IBBI மற்றும் BIS அங்கீகரித்த வழிகாட்டிகள்",
    "search_topics" to "கட்டுரைகள், மியூச்சுவல் ஃபண்ட், முதலீடுகளைத் தேடுங்கள்...",
    "categories" to "வகைகள்",
    "read_article" to "கட்டுரையைப் படிக்கவும்",
    "key_takeaways" to "முக்கிய குறிப்புகள்",
    "practical_example" to "நடைமுறை உதாரணம்",
    "official_source" to "அங்கீகரிக்கப்பட்ட மூலம்",

    "resources_title" to "அருகிலுள்ள அரசு நிதி அதிகாரங்கள்",
    "resources_subtitle" to "அங்கீகரிக்கப்பட்ட வங்கிகள், ஆலோசகர்கள், பத்திரப் பதிவு அலுவலகங்கள்",
    "search_resources" to "அதிகாரங்களை அல்லது முகவரியைத் தேடவும்...",
    "list_view" to "பட்டியல் காட்சி",
    "map_view" to "வரைபடக் காட்சி",
    "distance_km" to "கி.மீ தூரத்தில்",
    "verified_badge" to "சரிபார்க்கப்பட்டது",
    "unverified_badge" to "சரிபார்க்கப்படவில்லை",
    "needs_recheck_badge" to "மீண்டும் சரிபார்க்க வேண்டும்",
    "call_now" to "அழைக்கவும்",
    "open_map" to "வழித்தடம்",
    "report_incorrect" to "தவறான தகவலைப் புகாரளிக்கவும்",
    "official_authority" to "ஒழுங்குமுறை ஆணையம்",

    "documents_title" to "பாதுகாப்பான ஆவணப் பெட்டகம்",
    "documents_subtitle" to "தனிப்பட்ட மறைகுறியாக்கப்பட்ட நிதி ஆவணங்கள்",
    "upload_document" to "ஆவணம் பதிவேற்றவும்",
    "file_name" to "கோப்பு பெயர்",
    "file_size" to "கோப்பு அளவு",
    "upload_date" to "பதிவேற்றிய தேதி",
    "vault_description" to "காப்பீட்டு பாலிசிகள், ரசீதுகளைச் சாதனத்தில் பாதுகாக்கவும்",
    "no_documents" to "இன்னும் ஆவணங்கள் பதிவேற்றப்படவில்லை.",
    "save_to_vault" to "பெட்டகத்தில் சேமிக்கவும்",
    "select_file" to "கோப்பைத் தேர்ந்தெடுக்கவும் (PDF, PNG, JPG)",

    "notifications_title" to "அறிவிப்புகள் மற்றும் விழிப்பூட்டல்கள்",
    "notifications_subtitle" to "பட்ஜெட் எச்சரிக்கைகள் மற்றும் அரசு புதுப்பிப்புகள்",
    "mark_all_read" to "அனைத்தையும் படித்ததாகக் குறிக்கவும்",
    "no_notifications" to "தற்போது எந்த அறிவிப்புகளும் இல்லை.",

    "profile_title" to "என் சுயவிவரம் மற்றும் விருப்பங்கள்",
    "edit_profile" to "சுயவிவரத்தைத் திருத்து",
    "full_name" to "முழுப் பெயர்",
    "email_address" to "மின்னஞ்சல் முகவரி",
    "profession_type" to "தொழில் வகை",
    "address_section" to "இருப்பிட முகவரி",
    "address_line" to "தெரு / கதவு எண்",
    "city" to "நகரம்",
    "state" to "மாநிலம்",
    "postal_code" to "அஞ்சல் குறியீடு (PIN)",
    "preferred_language" to "விருப்பமான மொழி",
    "cloud_sync" to "Firestore கிளவுட் ஒத்திசைவு",
    "sync_to_cloud" to "கிளவுடுக்கு ஒத்திசை",
    "pull_to_room" to "கிளவுடில் இருந்து பதிவிறக்கு",
    "profile_updated_success" to "சுயவிவரம் வெற்றிகரமாகப் புதுப்பிக்கப்பட்டது!",

    "login_title" to "FinGuide-க்கு வரவேற்கிறோம்",
    "login_subtitle" to "உங்கள் நிதி மற்றும் வழிகாட்டல்களை அணுக உள்நுழைக",
    "register_title" to "கணக்கு உருவாக்கவும்",
    "register_subtitle" to "முகவரி மற்றும் மொழியுடன் பதிவு செய்யவும்",
    "email_label" to "மின்னஞ்சல்",
    "password_label" to "கடவுச்சொல்",
    "confirm_password_label" to "கடவுச்சொல்லை உறுதிப்படுத்து",
    "dont_have_account" to "கணக்கு இல்லையா?",
    "already_have_account" to "ஏற்கனவே கணக்கு உள்ளதா?",
    "admin_portal_access" to "நிர்வாக போர்டல் அணுகல்",
    "user_type_label" to "பயனர் வகை",
    "student" to "மாணவர்",
    "first_time_earner" to "முதல் முறை வருமானம் ஈட்டுபவர்",
    "working_professional" to "பணியாற்றும் தொழில்முறை",
    "address_required_error" to "முகவரி மற்றும் நகரம் காலியாக இருக்கக்கூடாது",
    "admin_login_title" to "நிர்வாக போர்டல் உள்நுழைவு",
    "admin_login_subtitle" to "அங்கீகரிக்கப்பட்ட நிர்வாகிகளுக்கான தனி அணுகல்",
    "admin_signin_button" to "நிர்வாக உள்நுழைவு",
    "switch_to_user_login" to "பயனர் உள்நுழைவுக்குத் திரும்பு",

    "admin_portal" to "நிர்வாக போர்டல்",
    "admin_dashboard" to "நிர்வாக டாஷ்போர்டு",
    "admin_users" to "பயனர் மேலாண்மை",
    "admin_resources" to "வளங்களின் பட்டியல்",
    "admin_verification" to "சரிபார்ப்பு மேசை",
    "admin_education" to "கல்வி மேலாளர்",
    "admin_reports" to "பயனர் புகார்கள்",
    "admin_notifications" to "பொது அறிவிப்புகள்",
    "admin_settings" to "அமைப்பு அமைப்புகள்",
    "total_users" to "மொத்த பயனர்கள்",
    "active_users" to "செயலில் உள்ள பயனர்கள்",
    "verified_resources" to "சரிபார்க்கப்பட்ட வளங்கள்",
    "pending_verification" to "நிலுவையில் உள்ளவை",
    "needs_recheck" to "மறுஆய்வு தேவைப்படுபவை",
    "reported_resources" to "புகாரளிக்கப்பட்டவை"
  )

  private val hiStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "पैसा प्रबंधित करें। संसाधन खोजें। बेहतर भविष्य बनाएं।",
    "nav_home" to "होम",
    "nav_finance" to "वित्त",
    "nav_learn" to "सीखें",
    "nav_resources" to "संसाधन",
    "nav_profile" to "प्रोफ़ाइल",
    "nav_documents" to "दस्तावेज़",
    "nav_notifications" to "सूचनाएं",
    "nav_admin" to "प्रशासन",
    "nav_settings" to "सेटिंग्स",
    "nav_budget" to "बजट",
    "nav_ai" to "AI चैट",

    "save" to "सहेजें",
    "cancel" to "रद्द करें",
    "edit" to "संपादित करें",
    "delete" to "हटाएं",
    "confirm" to "पुष्टि करें",
    "back" to "वापस",
    "close" to "बंद करें",
    "search" to "खोजें",
    "filter" to "फ़िल्टर",
    "upload" to "अपलोड",
    "download" to "डाउनलोड",
    "view" to "देखें",
    "retry" to "पुनः प्रयास",
    "submit" to "सबमिट करें",
    "clear" to "साफ़ करें",
    "next" to "आगे",
    "get_started" to "शुरू करें",
    "logout" to "लॉग आउट",
    "sign_in" to "साइन इन",
    "sign_up" to "साइन अप",
    "create_account" to "खाता बनाएं",
    "save_changes" to "बदलाव सहेजें",
    "view_all" to "सभी देखें",
    "details" to "विवरण",
    "status" to "स्थिति",
    "actions" to "क्रियाएं",

    "dashboard" to "डैशबोर्ड",
    "greeting_morning" to "शुभ प्रभात",
    "greeting_afternoon" to "शुभ दोपहर",
    "greeting_evening" to "शुभ संध्या",
    "total_balance" to "कुल शेष",
    "income" to "आय",
    "expenses" to "व्यय",
    "savings" to "बचत",
    "add_expense" to "+ व्यय जोड़ें",
    "add_income" to "+ आय जोड़ें",
    "budget" to "बजट",
    "savings_goal" to "बचत लक्ष्य",
    "voice_expense" to "आवाज़ से व्यय दर्ज करें",
    "recent_transactions" to "हाल के लेनदेन",
    "monthly_spending" to "मासिक खर्च",
    "quick_actions" to "त्वरित कार्य",
    "no_transactions_yet" to "अभी तक कोई लेनदेन दर्ज नहीं किया गया।",
    "financial_overview" to "वित्तीय अवलोकन",
    "smart_ai_insights" to "AI वित्तीय सलाह",

    "transactions_title" to "आय और व्यय खाता",
    "filter_all" to "सभी",
    "filter_today" to "आज",
    "filter_week" to "इस सप्ताह",
    "filter_month" to "इस महीने",
    "amount" to "राशि",
    "category" to "श्रेणी",
    "description" to "विवरण",
    "date" to "तारीख",
    "payment_mode" to "भुगतान विधि",
    "save_expense" to "व्यय सहेजें",
    "save_income" to "आय सहेजें",
    "edit_transaction" to "लेनदेन संपादित करें",
    "delete_transaction_confirm" to "क्या आप वाकई इस लेनदेन को हटाना चाहते हैं?",
    "search_placeholder" to "लेनदेन खोजें...",

    "budget_title" to "बजट और बचत लक्ष्य",
    "monthly_budget" to "मासिक बजट",
    "spent" to "खर्च किया",
    "remaining" to "शेष",
    "used" to "उपयोग किया",
    "set_budget" to "बजट निर्धारित करें",
    "category_budgets" to "श्रेणी बजट",
    "savings_title" to "बचत लक्ष्य",
    "target_amount" to "लक्ष्य राशि",
    "current_saved" to "वर्तमान बचत",
    "deadline" to "अंतिम तिथि",
    "add_goal" to "लक्ष्य जोड़ें",
    "deposit" to "जमा करें",
    "progress" to "प्रगति",

    "voice_expense_title" to "आवाज़ द्वारा व्यय प्रविष्टि",
    "speak_expense" to "अपनी भाषा में सामान्य रूप से बोलें",
    "voice_instruction" to "माइक दबाएं और बोलें: जैसे 'लंच के लिए 250 रुपये UPI से दिए'",
    "listening_tap_stop" to "सुन रहा है... रोकने के लिए टैप करें",
    "tap_to_speak" to "बोलने के लिए माइक दबाएं",
    "text_fallback_label" to "पहचाना गया टेक्स्ट",
    "voice_unavailable" to "इस डिवाइस पर वॉयस इनपुट उपलब्ध नहीं है।",
    "parse_and_review" to "व्यय निकालें",
    "enter_manually" to "हाथ से दर्ज करें",
    "did_you_mean" to "क्या आपका यह मतलब था?",
    "recording_started" to "आवाज़ रिकॉर्डिंग शुरू...",
    "recording_stopped" to "रिकॉर्डिंग समाप्त। समीक्षा करें।",

    "ai_assistant_title" to "FinGuide AI वित्तीय सहायक",
    "ai_subtitle" to "व्यक्तिगत मार्गदर्शन और प्रश्न",
    "ask_ai_placeholder" to "कोई भी वित्तीय प्रश्न पूछें...",
    "ask_button" to "पूछें",
    "suggested_questions" to "सुझाए गए प्रश्न",
    "disclaimer_ai" to "AI वित्तीय साक्षरता के लिए मार्गदर्शन प्रदान करता है। आधिकारिक पुष्टि हेतु पंजीकृत अधिकारियों से संपर्क करें।",

    "learn_title" to "वित्तीय शिक्षा केंद्र",
    "learn_subtitle" to "RBI, SEBI, IBBI और BIS द्वारा प्रमाणित गाइड",
    "search_topics" to "लेख, बचत, म्यूचुअल फंड खोजें...",
    "categories" to "श्रेणियां",
    "read_article" to "लेख पढ़ें",
    "key_takeaways" to "मुख्य बातें",
    "practical_example" to "व्यावहारिक उदाहरण",
    "official_source" to "आधिकारिक स्रोत",

    "resources_title" to "निकटतम वित्तीय प्राधिकरण",
    "resources_subtitle" to "सत्यापित बैंक, सलाहकार और रजिस्ट्री कार्यालय खोजें",
    "search_resources" to "प्राधिकरण या पता खोजें...",
    "list_view" to "सूची दृश्य",
    "map_view" to "मानचित्र दृश्य",
    "distance_km" to "किमी दूर",
    "verified_badge" to "सत्यापित",
    "unverified_badge" to "असत्यापित",
    "needs_recheck_badge" to "पुनर्जांच आवश्यक",
    "call_now" to "कॉल करें",
    "open_map" to "दिशा-निर्देश",
    "report_incorrect" to "गलत जानकारी की रिपोर्ट करें",
    "official_authority" to "नियामक प्राधिकरण",

    "documents_title" to "सुरक्षित दस्तावेज़ वॉल्ट",
    "documents_subtitle" to "निजी एन्क्रिप्टेड वित्तीय दस्तावेज़",
    "upload_document" to "दस्तावेज़ अपलोड करें",
    "file_name" to "फ़ाइल नाम",
    "file_size" to "फ़ाइल का आकार",
    "upload_date" to "अपलोड तिथि",
    "vault_description" to "बीमा पॉलिसी, रसीदें सुरक्षित रखें",
    "no_documents" to "अभी तक कोई दस्तावेज़ अपलोड नहीं हुआ।",
    "save_to_vault" to "वॉल्ट में सहेजें",
    "select_file" to "फ़ाइल चुनें (PDF, PNG, JPG)",

    "notifications_title" to "सूचनाएं और अलर्ट",
    "notifications_subtitle" to "बजट अलर्ट और सरकारी अपडेट",
    "mark_all_read" to "सभी को पढ़ा हुआ चिह्नित करें",
    "no_notifications" to "फ़िलहाल कोई सूचना नहीं है।",

    "profile_title" to "मेरी प्रोफ़ाइल और प्राथमिकताएं",
    "edit_profile" to "प्रोफ़ाइल संपादित करें",
    "full_name" to "पूरा नाम",
    "email_address" to "ईमेल पता",
    "profession_type" to "पेशा प्रकार",
    "address_section" to "आवासीय पता",
    "address_line" to "गली का पता / मकान नंबर",
    "city" to "शहर",
    "state" to "राज्य",
    "postal_code" to "पिन कोड",
    "preferred_language" to "पसंदीदा भाषा",
    "cloud_sync" to "Firestore क्लाउड सिंक",
    "sync_to_cloud" to "क्लाउड पर सिंक करें",
    "pull_to_room" to "क्लाउड से डाउनलोड करें",
    "profile_updated_success" to "प्रोफ़ाइल सफलतापूर्वक अपडेट की गई!",

    "login_title" to "FinGuide में स्वागत है",
    "login_subtitle" to "अपने वित्त और संसाधनों तक पहुँचने के लिए साइन इन करें",
    "register_title" to "अपना खाता बनाएं",
    "register_subtitle" to "पता और पसंदीदा भाषा के साथ रजिस्टर करें",
    "email_label" to "ईमेल",
    "password_label" to "पासवर्ड",
    "confirm_password_label" to "पासवर्ड की पुष्टि करें",
    "dont_have_account" to "खाता नहीं है?",
    "already_have_account" to "पहले से खाता है?",
    "admin_portal_access" to "प्रशासक पोर्टल",
    "user_type_label" to "उपयोगकर्ता प्रकार",
    "student" to "छात्र",
    "first_time_earner" to "पहली बार कमाने वाले",
    "working_professional" to "कार्यरत पेशेवर",
    "address_required_error" to "पता और शहर खाली नहीं हो सकते",
    "admin_login_title" to "प्रशासक पोर्टल लॉगिन",
    "admin_login_subtitle" to "केवल अधिकृत व्यवस्थापकों के लिए",
    "admin_signin_button" to "प्रशासक लॉगिन",
    "switch_to_user_login" to "उपयोगकर्ता लॉगिन पर वापस जाएं",

    "admin_portal" to "एडमिन पोर्टल",
    "admin_dashboard" to "एडमिन डैशबोर्ड",
    "admin_users" to "उपयोगकर्ता प्रबंधन",
    "admin_resources" to "संसाधन रजिस्ट्री",
    "admin_verification" to "सत्यापन डेस्क",
    "admin_education" to "शिक्षा प्रबंधक",
    "admin_reports" to "उपयोगकर्ता रिपोर्ट",
    "admin_notifications" to "प्रसारण अलर्ट",
    "admin_settings" to "सिस्टम सेटिंग्स",
    "total_users" to "कुल उपयोगकर्ता",
    "active_users" to "सक्रिय उपयोगकर्ता",
    "verified_resources" to "सत्यापित संसाधन",
    "pending_verification" to "लंबित सत्यापन",
    "needs_recheck" to "पुनर्जांच आवश्यक",
    "reported_resources" to "रिपोर्ट किए गए संसाधन"
  )

  private val teStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "డబ్బును నిర్వహించండి. వనరులను కనుగొనండి.",
    "nav_home" to "హోమ్",
    "nav_finance" to "ఆర్థికం",
    "nav_learn" to "నేర్చుకోండి",
    "nav_resources" to "వనరులు",
    "nav_profile" to "ప్రొఫైల్",
    "nav_documents" to "పత్రాలు",
    "nav_notifications" to "నోటిఫికేషన్లు",
    "nav_admin" to "అడ్మిన్",
    "nav_settings" to "సెట్టింగ్‌లు",
    "nav_budget" to "బడ్జెట్",
    "nav_ai" to "AI సంభాషణ",
    "dashboard" to "డాష్‌బోర్డ్",
    "total_balance" to "మొత్తం నిల్వ",
    "income" to "ఆదాయం",
    "expenses" to "ఖర్చులు",
    "savings" to "పొదుపు",
    "add_expense" to "ఖర్చు జోడించండి",
    "add_income" to "ఆదాయం జోడించండి",
    "budget" to "బడ్జెట్",
    "savings_goal" to "పొదుపు లక్ష్యం",
    "voice_expense" to "వాయిస్ ఖర్చు",
    "recent_transactions" to "ఇటీవలి లావాదేవీలు",
    "save" to "భద్రపరచు",
    "cancel" to "రద్దు చేయి",
    "confirm" to "నిర్ధారించు",
    "edit" to "సవరించు",
    "delete" to "తొలగించు",
    "search" to "వెతకండి",
    "upload" to "అప్‌లోడ్",
    "did_you_mean" to "మీ ఉద్దేశం ఇదేనా?",
    "admin_portal" to "FinGuide అడ్మిన్ పోర్టల్",
    "profile_title" to "ప్రొఫైల్ మరియు సెట్టింగ్‌లు",
    "resources_title" to "ధృవీకరించబడిన అధికారులు",
    "transactions_title" to "ఆదాయం & ఖర్చుల లెడ్జర్",
    "learn_title" to "ఆర్థిక విద్యా కేంద్రం",
    "documents_title" to "సురక్షిత పత్రాల ఖజానా",
    "notifications_title" to "నోటిఫికేషన్లు & హెచ్చరికలు",
    "logout" to "లాగ్ అవుట్",
    "sign_out" to "లాగ్ అవుట్",
    "quick_actions" to "శీఘ్ర చర్యలు",
    "monthly_spending" to "నెలవారీ ఖర్చు"
  )

  private val knStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "ಹಣವನ್ನು ನಿರ್ವಹಿಸಿ. ಸಂಪನ್ಮೂಲಗಳನ್ನು ಅನ್ವೇಷಿಸಿ.",
    "nav_home" to "ಮುಖಪುಟ",
    "nav_finance" to "ಹಣಕಾಸು",
    "nav_learn" to "ಕಲಿಯಿರಿ",
    "nav_resources" to "ಸಂಪನ್ಮೂಲಗಳು",
    "nav_profile" to "ಪ್ರೊಫೈಲ್",
    "nav_documents" to "ದಾಖಲೆಗಳು",
    "nav_notifications" to "ಸೂಚನೆಗಳು",
    "nav_admin" to "ಅಡ್ಮಿನ್",
    "nav_settings" to "ಸೆಟ್ಟಿಂಗ್‌ಗಳು",
    "nav_budget" to "ಬಜೆಟ್",
    "nav_ai" to "AI ಚಾಟ್",
    "dashboard" to "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್",
    "total_balance" to "ಒಟ್ಟು ಬಾಕಿ",
    "income" to "ಆದಾಯ",
    "expenses" to "ವೆಚ್ಚಗಳು",
    "savings" to "ಉಳಿತಾಯ",
    "add_expense" to "ವೆಚ್ಚ ಸೇರಿಸಿ",
    "add_income" to "ಆದಾಯ ಸೇರಿಸಿ",
    "budget" to "ಬಜೆಟ್",
    "savings_goal" to "ಉಳಿತಾಯ ಗುರಿ",
    "voice_expense" to "ಧ್ವನಿ ವೆಚ್ಚ",
    "recent_transactions" to "ಇತ್ತೀಚಿನ ವಹಿವಾಟುಗಳು",
    "save" to "ಉಳಿಸಿ",
    "cancel" to "ರದ್ದುಮಾಡಿ",
    "confirm" to "ದೃಢೀಕರಿಸಿ",
    "edit" to "ತಿದ್ದಿ",
    "delete" to "ಅಳಿಸಿ",
    "search" to "ಹುಡುಕಿ",
    "upload" to "ಅಪ್‌ಲೋಡ್",
    "did_you_mean" to "ನೀವು ಹೇಳಿದ್ದು ಇದನ್ನೇನಾ?",
    "admin_portal" to "FinGuide ಅಡ್ಮಿನ್ ಪೋರ್ಟಲ್",
    "profile_title" to "ಪ್ರೊಫೈಲ್ ಮತ್ತು ಸೆಟ್ಟಿಂಗ್‌ಗಳು",
    "resources_title" to "ದೃಢೀಕರಿಸಿದ ಪ್ರಾಧಿಕಾರಗಳು",
    "transactions_title" to "ಆದಾಯ ಮತ್ತು ವೆಚ್ಚದ ಲೆಡ್ಜರ್",
    "learn_title" to "ಹಣಕಾಸು ಶಿಕ್ಷಣ ಕೇಂದ್ರ",
    "documents_title" to "ಸುರಕ್ಷಿತ ದಾಖಲೆ ವಾಲ್ಟ್",
    "notifications_title" to "ಸೂಚನೆಗಳು ಮತ್ತು ಎಚ್ಚರಿಕೆಗಳು",
    "logout" to "ಲಾಗ್ ಔಟ್",
    "sign_out" to "ಲಾಗ್ ಔಟ್",
    "quick_actions" to "ತ್ವರಿತ ಕ್ರಿಯೆಗಳು",
    "monthly_spending" to "ಮಾಸಿಕ ವೆಚ್ಚ"
  )

  private val mlStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "പണം കൈകാര്യം ചെയ്യുക. ഉറവിടങ്ങൾ കണ്ടെത്തുക.",
    "nav_home" to "ഹോം",
    "nav_finance" to "ധനകാര്യം",
    "nav_learn" to "പഠിക്കുക",
    "nav_resources" to "വിഭവങ്ങൾ",
    "nav_profile" to "പ്രൊഫൈൽ",
    "nav_documents" to "രേഖകൾ",
    "nav_notifications" to "അറിയിപ്പുകൾ",
    "nav_admin" to "അഡ്മിൻ",
    "nav_settings" to "ക്രമീകരണങ്ങൾ",
    "nav_budget" to "ബജറ്റ്",
    "nav_ai" to "AI ചാറ്റ്",
    "dashboard" to "ഡാഷ്‌ബോർഡ്",
    "total_balance" to "ആകെ ബാലൻസ്",
    "income" to "വരുമാനം",
    "expenses" to "ചെലവുകൾ",
    "savings" to "സമ്പാദ്യം",
    "add_expense" to "ചെലവ് ചേർക്കുക",
    "add_income" to "വരുമാനം ചേർക്കുക",
    "budget" to "ബജറ്റ്",
    "savings_goal" to "സമ്പാദ്യ ലക്ഷ്യം",
    "voice_expense" to "ശബ്ദ ചെലവ്",
    "recent_transactions" to "സമീപകാല ഇടപാടുകൾ",
    "save" to "സൂക്ഷിക്കുക",
    "cancel" to "റദ്ദാക്കുക",
    "confirm" to "സ്ഥിരീകരിക്കുക",
    "edit" to "തിരുത്തുക",
    "delete" to "ഒഴിവാക്കുക",
    "search" to "തിരയുക",
    "upload" to "അപ്‌ലോഡ്",
    "did_you_mean" to "നിങ്ങൾ ഉദ്ദേശിച്ചത് ഇതാണോ?",
    "admin_portal" to "FinGuide അഡ്മിൻ പോർട്ടൽ",
    "profile_title" to "പ്രൊഫൈലും ക്രമീകരണങ്ങളും",
    "resources_title" to "സ്ഥിരീകരിച്ച അധികാരികൾ",
    "transactions_title" to "വരവ് ചെലവ് ലെഡ്ജർ",
    "learn_title" to "സാമ്പത്തിക വിദ്യാഭ്യാസ കേന്ദ്രം",
    "documents_title" to "സുരക്ഷിത രേഖ നിലവറ",
    "notifications_title" to "അറിയിപ്പുകളും മുന്നറിയിപ്പുകളും",
    "logout" to "ലോഗ് ഔട്ട്",
    "sign_out" to "ലോഗ് ഔട്ട്",
    "quick_actions" to "ദ്രുത പ്രവർത്തനങ്ങൾ",
    "monthly_spending" to "പ്രതിമാസ ചെലവ്"
  )

  private val bnStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "অর্থ পরিচালনা করুন। সম্পদ আবিষ্কার করুন।",
    "nav_home" to "হোম",
    "nav_finance" to "অর্থ",
    "nav_learn" to "শিখুন",
    "nav_resources" to "সম্পদ",
    "nav_profile" to "প্রোফাইল",
    "nav_documents" to "নথিপত্র",
    "nav_notifications" to "বিজ্ঞপ্তি",
    "nav_admin" to "অ্যাডমিন",
    "nav_settings" to "সেটিংস",
    "nav_budget" to "বাজেট",
    "nav_ai" to "AI চ্যাট",
    "dashboard" to "ড্যাশবোর্ড",
    "total_balance" to "মোট ব্যালেন্স",
    "income" to "আয়",
    "expenses" to "ব্যয়",
    "savings" to "সঞ্চয়",
    "add_expense" to "ব্যয় যোগ করুন",
    "add_income" to "আয় যোগ করুন",
    "budget" to "বাজেট",
    "savings_goal" to "সঞ্চয় লক্ষ্য",
    "voice_expense" to "ভয়েস ব্যয়",
    "recent_transactions" to "সাম্প্রতিক লেনদেন",
    "save" to "সংরক্ষণ",
    "cancel" to "বাতিল",
    "confirm" to "নিশ্চিত করুন",
    "edit" to "সম্পাদনা",
    "delete" to "মুছুন",
    "search" to "অনুসন্ধান",
    "upload" to "আপলোড",
    "did_you_mean" to "আপনি কি এটি বোঝাতে চেয়েছেন?",
    "admin_portal" to "FinGuide অ্যাডমিন পোর্টাল",
    "profile_title" to "প্রোফাইল এবং সেটিংস",
    "resources_title" to "যাচাইকৃত কর্তৃপক্ষ",
    "transactions_title" to "আয় ও ব্যয়ের খাতা",
    "learn_title" to "আর্থিক শিক্ষা হাব",
    "documents_title" to "নিরাপদ নথি ভল্ট",
    "notifications_title" to "বিজ্ঞপ্তি ও সতর্কতা",
    "logout" to "লগ আউট",
    "sign_out" to "লগ আউট",
    "quick_actions" to "দ্রুত অ্যাকশন",
    "monthly_spending" to "মাসিক খরচ"
  )

  private val mrStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "पैशाचे व्यवस्थापन करा. अधिकृत संस्था शोधा.",
    "nav_home" to "मुख्यपृष्ठ",
    "nav_finance" to "वित्त",
    "nav_learn" to "शिका",
    "nav_resources" to "संसाधने",
    "nav_profile" to "प्रोफाइल",
    "nav_documents" to "दस्तऐवज",
    "nav_notifications" to "सूचना",
    "nav_admin" to "प्रशासन",
    "nav_settings" to "सेटिंग्ज",
    "nav_budget" to "बजेट",
    "nav_ai" to "AI चॅट",
    "dashboard" to "डॅशबोर्ड",
    "total_balance" to "एकूण शिल्लक",
    "income" to "उत्पन्न",
    "expenses" to "खर्च",
    "savings" to "बचत",
    "add_expense" to "खर्च जोडा",
    "add_income" to "उत्पन्न जोडा",
    "budget" to "बजेट",
    "savings_goal" to "बचत ध्येय",
    "voice_expense" to "व्हॉइस खर्च",
    "recent_transactions" to "अलीकडील व्यवहार",
    "save" to "जतन करा",
    "cancel" to "रद्द करा",
    "confirm" to "पुष्टी करा",
    "edit" to "संपादित करा",
    "delete" to "हटवा",
    "search" to "शोधा",
    "upload" to "अपलोड",
    "did_you_mean" to "तुम्हाला हे म्हणायचे होते का?",
    "admin_portal" to "FinGuide प्रशासन पोर्टल",
    "profile_title" to "प्रोफाइल आणि सेटिंग्ज",
    "resources_title" to "प्रमाणित प्राधिकरणे",
    "transactions_title" to "उत्पन्न आणि खर्च खाते",
    "learn_title" to "आर्थिक शिक्षण केंद्र",
    "documents_title" to "सुरक्षित दस्तऐवज व्हॉल्ट",
    "notifications_title" to "सूचना आणि अलर्ट",
    "logout" to "लॉग आउट",
    "sign_out" to "लॉग आउट",
    "quick_actions" to "जलद क्रिया",
    "monthly_spending" to "मासिक खर्च"
  )

  private val guStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "નાણાંનું સંચાલન કરો. સંસાધનો શોધો.",
    "nav_home" to "હોમ",
    "nav_finance" to "નાણાકીય",
    "nav_learn" to "શીખો",
    "nav_resources" to "સંસાધનો",
    "nav_profile" to "પ્રોફાઇલ",
    "nav_documents" to "દસ્તાવેજો",
    "nav_notifications" to "સૂચનાઓ",
    "nav_admin" to "એડમિન",
    "nav_settings" to "સેટિંગ્સ",
    "nav_budget" to "બજેટ",
    "nav_ai" to "AI ચેટ",
    "dashboard" to "ડેશબોર્ડ",
    "total_balance" to "કુલ સિલક",
    "income" to "આવક",
    "expenses" to "ખર્ચ",
    "savings" to "બચત",
    "add_expense" to "ખર્ચ ઉમેરો",
    "add_income" to "આવક ઉમેરો",
    "budget" to "બજેટ",
    "savings_goal" to "બચત લક્ષ્ય",
    "voice_expense" to "વોઇસ ખર્ચ",
    "recent_transactions" to "તાજેતરના વ્યવહારો",
    "save" to "સાચવો",
    "cancel" to "રદ કરો",
    "confirm" to "પુષ્ટિ કરો",
    "edit" to "સંપાદિત કરો",
    "delete" to "કાઢી નાખો",
    "search" to "શોધો",
    "upload" to "અપલોડ",
    "did_you_mean" to "શું તમારો આ મતલબ હતો?",
    "admin_portal" to "FinGuide એડમિન પોર્ટલ",
    "profile_title" to "પ્રોફાઇલ અને સેટિંગ્સ",
    "resources_title" to "ચકાસાયેલ સત્તાવાળાઓ",
    "transactions_title" to "આવક અને ખર્ચ ખાતાવહી",
    "learn_title" to "નાણાકીય શિક્ષણ હબ",
    "documents_title" to "સુરક્ષિત દસ્તાવેજ વૉલ્ટ",
    "notifications_title" to "સૂચનાઓ અને ચેતવણીઓ",
    "logout" to "લૉગ આઉટ",
    "sign_out" to "લૉગ આઉટ",
    "quick_actions" to "ઝડપી ક્રિયાઓ",
    "monthly_spending" to "માસિક ખર્ચ"
  )

  private val paStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "ਪੈਸੇ ਦਾ ਪ੍ਰਬੰਧਨ ਕਰੋ। ਸਰੋਤ ਖੋਜੋ।",
    "nav_home" to "ਹੋਮ",
    "nav_finance" to "ਵਿੱਤ",
    "nav_learn" to "ਸਿੱਖੋ",
    "nav_resources" to "ਸਰੋਤ",
    "nav_profile" to "ਪ੍ਰੋਫਾਈਲ",
    "nav_documents" to "ਦਸਤਾਵੇਜ਼",
    "nav_notifications" to "ਸੂਚਨਾਵਾਂ",
    "nav_admin" to "ਐਡਮਿਨ",
    "nav_settings" to "ਸੈਟਿੰਗਾਂ",
    "nav_budget" to "ਬਜਟ",
    "nav_ai" to "AI ਗੱਲਬਾਤ",
    "dashboard" to "ਡੈਸ਼ਬੋਰਡ",
    "total_balance" to "ਕੁੱਲ ਬਕਾਇਆ",
    "income" to "ਆਮਦਨ",
    "expenses" to "ਖਰਚੇ",
    "savings" to "ਬੱਚਤ",
    "add_expense" to "ਖਰਚਾ ਸ਼ਾਮਲ ਕਰੋ",
    "add_income" to "ਆਮਦਨ ਸ਼ਾਮਲ ਕਰੋ",
    "budget" to "ਬਜਟ",
    "savings_goal" to "ਬੱਚਤ ਟੀਚਾ",
    "voice_expense" to "ਆਵਾਜ਼ ਦੁਆਰਾ ਖਰਚਾ",
    "recent_transactions" to "ਹਾਲੀਆ ਲੈਣ-ਦੇਣ",
    "save" to "ਸੰਭਾਲੋ",
    "cancel" to "ਰੱਦ ਕਰੋ",
    "confirm" to "ਪੁਸ਼ਟੀ ਕਰੋ",
    "edit" to "ਸੋਧੋ",
    "delete" to "ਮਿਟਾਓ",
    "search" to "ਖੋਜੋ",
    "upload" to "ਅੱਪਲੋਡ",
    "did_you_mean" to "ਕੀ ਤੁਹਾਡਾ ਇਹ ਮਤਲਬ ਸੀ?",
    "admin_portal" to "FinGuide ਐਡਮਿਨ ਪੋਰਟਲ",
    "profile_title" to "ਪ੍ਰੋਫਾਈਲ ਅਤੇ ਸੈਟਿੰਗਾਂ",
    "resources_title" to "ਤਸਦੀਕਸ਼ੁਦਾ ਅਥਾਰਟੀਆਂ",
    "transactions_title" to "ਆਮਦਨ ਅਤੇ ਖਰਚਾ ਖਾਤਾ",
    "learn_title" to "ਵਿੱਤੀ ਸਿੱਖਿਆ ਕੇਂਦਰ",
    "documents_title" to "ਸੁਰੱਖਿਅਤ ਦਸਤਾਵੇਜ਼ ਵਾਲਟ",
    "notifications_title" to "ਸੂਚਨਾਵਾਂ ਅਤੇ ਅਲਰਟ",
    "logout" to "ਲਾਗ ਆਉਟ",
    "sign_out" to "ਲਾਗ ਆਉਟ",
    "quick_actions" to "ਤੁਰੰਤ ਕਾਰਵਾਈਆਂ",
    "monthly_spending" to "ਮਹੀਨਾਵਾਰ ਖਰਚਾ"
  )

  private val orStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "ଅର୍ଥ ପରିଚାଳନା କରନ୍ତୁ। ସମ୍ବଳ ଖୋଜନ୍ତୁ।",
    "nav_home" to "ମୂଳପୃଷ୍ଠା",
    "nav_finance" to "ବିତ୍ତ",
    "nav_learn" to "ଶିଖନ୍ତୁ",
    "nav_resources" to "ସମ୍ବଳ",
    "nav_profile" to "ପ୍ରୋଫାଇଲ୍",
    "nav_documents" to "ଦଲିଲ",
    "nav_notifications" to "ବିଜ୍ଞପ୍ତି",
    "nav_admin" to "ପ୍ରଶାସକ",
    "nav_settings" to "ସେଟିଂସ୍",
    "nav_budget" to "ବଜେଟ୍",
    "nav_ai" to "AI ଚାଟ୍",
    "dashboard" to "ଡ୍ୟାସବୋର୍ଡ",
    "total_balance" to "ମୋଟ ବାଲାନ୍ସ",
    "income" to "ଆୟ",
    "expenses" to "ଖର୍ଚ୍ଚ",
    "savings" to "ସଞ୍ଚୟ",
    "add_expense" to "ଖର୍ଚ୍ଚ ଯୋଡନ୍ତୁ",
    "add_income" to "ଆୟ ଯୋଡନ୍ତୁ",
    "budget" to "ବଜେଟ୍",
    "savings_goal" to "ସଞ୍ଚୟ ଲକ୍ଷ୍ୟ",
    "voice_expense" to "ଭଏସ୍ ଖର୍ଚ୍ଚ",
    "recent_transactions" to "ସାମ୍ପ୍ରତିକ କାରବାର",
    "save" to "ସାଇତନ୍ତୁ",
    "cancel" to "ବାତିଲ୍",
    "confirm" to "ନିଶ୍ଚିତ କରନ୍ତୁ",
    "edit" to "ସମ୍ପାଦନ",
    "delete" to "ଡିଲିଟ୍",
    "search" to "ଖୋଜନ୍ତୁ",
    "upload" to "ଅପଲୋଡ୍",
    "did_you_mean" to "ଆପଣ ଏହା କହିବାକୁ ଚାହୁଁଥିଲେ କି?",
    "admin_portal" to "FinGuide ପ୍ରଶାସକ ପୋର୍ଟାଲ୍",
    "profile_title" to "ପ୍ରୋଫାଇଲ୍ ଏବଂ ସେଟିଂସ୍",
    "resources_title" to "ଯାଞ୍ଚ ହୋଇଥିବା ପ୍ରାଧିକରଣ",
    "transactions_title" to "ଆୟ ଏବଂ ଖର୍ଚ୍ଚ ଖାତା",
    "learn_title" to "ଆର୍ଥିକ ଶିକ୍ଷା କେନ୍ଦ୍ର",
    "documents_title" to "ସୁରକ୍ଷିତ ଦଲିଲ ଭଲ୍ଟ",
    "notifications_title" to "ବିଜ୍ଞପ୍ତି ଏବଂ ସତର୍କତା",
    "logout" to "ଲଗ୍ ଆଉଟ୍",
    "sign_out" to "ଲଗ୍ ଆଉଟ୍",
    "quick_actions" to "ଦ୍ରୁତ କାର୍ଯ୍ୟ",
    "monthly_spending" to "ମାସିକ ଖର୍ଚ୍ଚ"
  )

  private val asStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "টকা-পইচা পৰিচালনা কৰক। সম্পদ বিচাৰক।",
    "nav_home" to "ঘৰ",
    "nav_finance" to "বিত্ত",
    "nav_learn" to "শিকক",
    "nav_resources" to "সম্পদ",
    "nav_profile" to "প্রোফাইল",
    "nav_documents" to "নথিপত্ৰ",
    "nav_notifications" to "জাননী",
    "nav_admin" to "এডমিন",
    "nav_settings" to "ছেটিংছ",
    "nav_budget" to "বাজেট",
    "nav_ai" to "AI বাৰ্তালাপ",
    "dashboard" to "ডেশ্ববৰ্ড",
    "total_balance" to "মুঠ জমা",
    "income" to "আয়",
    "expenses" to "ব্যয়",
    "savings" to "সঞ্চয়",
    "add_expense" to "ব্যয় যোগ কৰক",
    "add_income" to "আয় যোগ কৰক",
    "budget" to "বাজেট",
    "savings_goal" to "সঞ্চয় লক্ষ্য",
    "voice_expense" to "কণ্ঠস্বৰ ব্যয়",
    "recent_transactions" to "শেহতীয়া লেনদেন",
    "save" to "সংৰক্ষণ",
    "cancel" to "বাতিল",
    "confirm" to "নিশ্চিত কৰক",
    "edit" to "সম্পাদনা",
    "delete" to "মচি পেলাওক",
    "search" to "সন্ধান কৰক",
    "upload" to "আপলোড",
    "did_you_mean" to "আপুনি এইটো বুজাব খুজিছিল নেকি?",
    "admin_portal" to "FinGuide এডমিন পৰ্টেল",
    "profile_title" to "প্রোফাইল আৰু ছেটিংছ",
    "resources_title" to "পৰীক্ষিত কৰ্তৃপক্ষ",
    "transactions_title" to "আয় আৰু ব্যয়ৰ খতিয়ান",
    "learn_title" to "বিত্তীয় শিক্ষা কেন্দ্ৰ",
    "documents_title" to "সুৰক্ষিত নথি ভল্ট",
    "notifications_title" to "জাননী আৰু সতৰ্কবাৰ্তা",
    "logout" to "লগ আউট",
    "sign_out" to "লগ আউট",
    "quick_actions" to "দ্ৰুত কাৰ্য্য",
    "monthly_spending" to "মাহেকীয়া খৰচ"
  )

  private val urStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "پیسے کا انتظام کریں۔ سرکاری وسائل دریافت کریں۔",
    "nav_home" to "ہوم",
    "nav_finance" to "مالیات",
    "nav_learn" to "سیکھیں",
    "nav_resources" to "وسائل",
    "nav_profile" to "پروفائل",
    "nav_documents" to "دستاویزات",
    "nav_notifications" to "اطلاعات",
    "nav_admin" to "ایڈمن",
    "nav_settings" to "ترتیبات",
    "nav_budget" to "بجٹ",
    "nav_ai" to "AI چیٹ",
    "dashboard" to "ڈیش بورڈ",
    "total_balance" to "کل بیلنس",
    "income" to "آمدنی",
    "expenses" to "اخراجات",
    "savings" to "بچت",
    "add_expense" to "خرچ شامل کریں",
    "add_income" to "آمدنی شامل کریں",
    "budget" to "بجٹ",
    "savings_goal" to "بچت کا ہدف",
    "voice_expense" to "آواز سے خرچ درج کریں",
    "recent_transactions" to "حالیہ لین دین",
    "save" to "محفوظ کریں",
    "cancel" to "منسوخ",
    "confirm" to "تصدیق کریں",
    "edit" to "ترمیم",
    "delete" to "حذف کریں",
    "search" to "تلاش کریں",
    "upload" to "اپ لوڈ",
    "did_you_mean" to "کیا آپ کا مطلب یہ تھا؟",
    "admin_portal" to "FinGuide ایڈمن پورٹل",
    "profile_title" to "پروفائل اور ترتیبات",
    "resources_title" to "تصدیق شدہ حکام",
    "transactions_title" to "آمدنی اور اخراجات لیجر",
    "learn_title" to "مالیاتی تعلیمی مرکز",
    "documents_title" to "محفوظ دستاویزات والٹ",
    "notifications_title" to "اطلاعات اور انتباہات",
    "logout" to "لاگ آؤٹ",
    "sign_out" to "لاگ آؤٹ",
    "quick_actions" to "فوری اقدامات",
    "monthly_spending" to "ماہانہ اخراجات"
  )

  private val neStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "पैसा व्यवस्थापन गर्नुहोस्। स्रोतहरू खोज्नुहोस्।",
    "nav_home" to "गृहपृष्ठ",
    "nav_finance" to "वित्त",
    "nav_learn" to "सिक्नुहोस्",
    "nav_resources" to "स्रोतहरू",
    "nav_profile" to "प्रोफाइल",
    "nav_documents" to "कागजातहरू",
    "nav_notifications" to "सूचनाहरू",
    "nav_admin" to "व्यवस्थापक",
    "nav_settings" to "सेटिङहरू",
    "nav_budget" to "बजेट",
    "nav_ai" to "AI कुराकानी",
    "dashboard" to "ड्यासबोर्ड",
    "total_balance" to "कुल मौज्दात",
    "income" to "आम्दानी",
    "expenses" to "खर्च",
    "savings" to "बचत",
    "add_expense" to "खर्च थप्नुहोस्",
    "add_income" to "आम्दानी थप्नुहोस्",
    "budget" to "बजेट",
    "savings_goal" to "बचत लक्ष्य",
    "voice_expense" to "आवाजबाट खर्च",
    "recent_transactions" to "हालैका कारोबारहरू",
    "save" to "सुरक्षित गर्नुहोस्",
    "cancel" to "रद्द गर्नुहोस्",
    "confirm" to "पुष्टि गर्नुहोस्",
    "edit" to "सम्पादन",
    "delete" to "हटाउनुहोस्",
    "search" to "खोज्नुहोस्",
    "upload" to "अपलोड",
    "did_you_mean" to "के तपाईँको मतलब यही थियो?",
    "admin_portal" to "FinGuide व्यवस्थापक पोर्टल",
    "profile_title" to "प्रोफाइल र सेटिङहरू",
    "resources_title" to "प्रमाणित अधिकारीहरू",
    "transactions_title" to "आम्दानी र खर्च खाता",
    "learn_title" to "वित्तीय शिक्षा केन्द्र",
    "documents_title" to "सुरक्षित कागजात भल्ट",
    "notifications_title" to "सूचना र चेतावनीहरू",
    "logout" to "लग आउट",
    "sign_out" to "लग आउट",
    "quick_actions" to "द्रुत कार्यहरू",
    "monthly_spending" to "मासिक खर्च"
  )

  private val kokStrings = mapOf(
    "app_name" to "FinGuide",
    "tagline" to "पयशे वेवस्थापन करात. अधिकृत संस्था सोधात.",
    "nav_home" to "मुखपृष्ठ",
    "nav_finance" to "वित्त",
    "nav_learn" to "शिका",
    "nav_resources" to "संसाधनां",
    "nav_profile" to "प्रोफाइल",
    "nav_documents" to "दस्तावेज",
    "nav_notifications" to "सुचोवणी",
    "nav_admin" to "अॅडमिन",
    "nav_settings" to "सेटिंग्स",
    "nav_budget" to "बजेट",
    "nav_ai" to "AI संवाद",
    "dashboard" to "डॅशबोर्ड",
    "total_balance" to "एकूण शिल्लक",
    "income" to "येणावळ",
    "expenses" to "खर्च",
    "savings" to "बचत",
    "add_expense" to "खर्च जोडात",
    "add_income" to "येणावळ जोडात",
    "budget" to "बजेट",
    "savings_goal" to "बचत ध्येय",
    "voice_expense" to "आवाजान खर्च",
    "recent_transactions" to "हालींचे व्यवहार",
    "save" to "सांबाळा",
    "cancel" to "रद्द करा",
    "confirm" to "खात्री करा",
    "edit" to "बदला",
    "delete" to "काडून उडय",
    "search" to "सोदात",
    "upload" to "अपलोड",
    "did_you_mean" to "तुमकां हेंच म्हणचें आशिल्ले?",
    "admin_portal" to "FinGuide अॅडमिन पोर्टल",
    "profile_title" to "प्रोफाइल आनी सेटिंग्स",
    "resources_title" to "तपासिल्लीं प्राधिकरणां",
    "transactions_title" to "येणावळ आनी खर्च खाते",
    "learn_title" to "आर्थिक शिक्षण केंद्र",
    "documents_title" to "सुरक्षीत दस्तावेज व्हॉल्ट",
    "notifications_title" to "सुचोवण्या आनी शिटकावण्यो",
    "logout" to "लॉग आऊट",
    "sign_out" to "लॉग आऊट",
    "quick_actions" to "रोकडीं कृत्यां",
    "monthly_spending" to "म्हयन्याचो खर्च"
  )

  private val langDictionary = mapOf(
    "en" to enStrings,
    "ta" to taStrings,
    "hi" to hiStrings,
    "te" to teStrings,
    "kn" to knStrings,
    "ml" to mlStrings,
    "bn" to bnStrings,
    "mr" to mrStrings,
    "gu" to guStrings,
    "pa" to paStrings,
    "or" to orStrings,
    "as" to asStrings,
    "ur" to urStrings,
    "ne" to neStrings,
    "kok" to kokStrings
  )

  // Reverse mapping from English string/phrase to localization key
  private val textToKeyMap: Map<String, String> by lazy {
    val map = HashMap<String, String>()
    enStrings.forEach { (k, v) ->
      map[v.trim().lowercase()] = k
      map[k.trim().lowercase()] = k
    }
    // Explicit UI matches
    map["home"] = "nav_home"
    map["finance"] = "nav_finance"
    map["ledger"] = "transactions_title"
    map["learn"] = "nav_learn"
    map["education"] = "nav_learn"
    map["resources"] = "nav_resources"
    map["authorities"] = "resources_title"
    map["profile"] = "nav_profile"
    map["account"] = "profile_title"
    map["documents"] = "nav_documents"
    map["vault"] = "documents_title"
    map["alerts"] = "notifications_title"
    map["notifications"] = "notifications_title"
    map["admin"] = "nav_admin"
    map["admin portal"] = "admin_portal"
    map["finguide admin portal"] = "admin_portal"
    map["settings"] = "nav_settings"
    map["budgets"] = "nav_budget"
    map["budget"] = "budget"
    map["savings"] = "savings"
    map["ai chat"] = "nav_ai"
    map["ai advisor"] = "nav_ai"
    map["voice expense"] = "voice_expense"
    map["add expense"] = "add_expense"
    map["add income"] = "add_income"
    map["recent transactions"] = "recent_transactions"
    map["total balance"] = "total_balance"
    map["sign out"] = "logout"
    map["log out"] = "logout"
    map["logout"] = "logout"
    map["profile & settings"] = "profile_title"
    map["verified authorities"] = "resources_title"
    map["income & expense ledger"] = "transactions_title"
    map["financial education hub"] = "learn_title"
    map["secure document vault"] = "documents_title"
    map["notifications & alerts"] = "notifications_title"
    map["my profile & preferences"] = "profile_title"
    map["quick actions"] = "quick_actions"
    map["monthly spending"] = "monthly_spending"
    map["all"] = "filter_all"
    map["today"] = "filter_today"
    map["this week"] = "filter_week"
    map["this month"] = "filter_month"
    map["amount"] = "amount"
    map["category"] = "category"
    map["description"] = "description"
    map["date"] = "date"
    map["payment mode"] = "payment_mode"
    map["search"] = "search"
    map["cancel"] = "cancel"
    map["confirm"] = "confirm"
    map["save"] = "save"
    map["edit"] = "edit"
    map["delete"] = "delete"
    map["back"] = "back"
    map["close"] = "close"
    map["submit"] = "submit"
    map["upload"] = "upload"
    map["expenses"] = "expenses"
    map["income"] = "income"
    map["savings"] = "savings"
    map["savings goal"] = "savings_goal"
    map["savings stash"] = "savings"
    map["+ expense"] = "add_expense"
    map["+ income"] = "add_income"
    map["voice"] = "voice_expense"
    map["view all"] = "view_all"
    map["welcome back,"] = "dashboard"
    map["welcome back"] = "dashboard"
    map["net available balance"] = "total_balance"
    map["available funds"] = "total_balance"
    map["net available"] = "total_balance"
    map["earned this cycle"] = "income"
    map["spent this cycle"] = "expenses"
    map["monthly budget"] = "monthly_budget"
    map["spent"] = "spent"
    map["remaining"] = "remaining"
    map["used"] = "used"
    map["category budgets"] = "category_budgets"
    map["set category budget"] = "set_budget"
    map["add savings goal"] = "add_goal"
    map["add goal"] = "add_goal"
    map["target amount"] = "target_amount"
    map["current saved"] = "current_saved"
    map["deadline"] = "deadline"
    map["deposit"] = "deposit"
    map["progress"] = "progress"
    map["save expense"] = "save_expense"
    map["save income"] = "save_income"
    map["edit transaction"] = "edit_transaction"
    map["delete transaction"] = "delete"
    map["financial overview"] = "financial_overview"
    map["finguide ai insight"] = "smart_ai_insights"
    map["ai financial insight"] = "smart_ai_insights"
    map["preferred location"] = "resources_title"
    map["in your preferred location"] = "resources_title"
    map["speak your expense"] = "speak_expense"
    map["tap microphone to speak"] = "tap_to_speak"
    map["listening... tap to stop"] = "listening_tap_stop"
    map["try example spoken phrases:"] = "speak_expense"
    map["or type spoken phrase manually (text fallback):"] = "text_fallback_label"
    map["parse & review expense"] = "parse_and_review"
    map["category spending breakdown"] = "monthly_spending"
    map["budget progress"] = "budget"
    map["active goals"] = "savings"
    map["entries"] = "transactions_title"
    map["sign in"] = "sign_in"
    map["sign up"] = "sign_up"
    map["register"] = "sign_up"
    map
  }

  /**
   * Translates any string (either a resource key or an English UI label/phrase)
   * into the target language code.
   */
  fun translate(textOrKey: String?, langCode: String = "en"): String {
    if (textOrKey.isNullOrBlank()) return ""
    if (langCode == "en") return textOrKey

    val trimmed = textOrKey.trim()
    val dict = langDictionary[langCode] ?: langDictionary["en"] ?: return textOrKey

    // 1. Direct key match
    val direct = dict[trimmed]
    if (!direct.isNullOrBlank()) return direct

    // 2. Reverse lookup by lowercase English phrase
    val key = textToKeyMap[trimmed.lowercase()]
    if (key != null) {
      val localized = dict[key]
      if (!localized.isNullOrBlank()) return localized
      val fallbackEn = enStrings[key]
      if (!fallbackEn.isNullOrBlank()) return fallbackEn
    }

    return trimmed
  }

  /**
   * Retrieves translated string for key in target language.
   * Falls back to English if missing in target language, or key itself if missing in English.
   */
  fun getString(key: String, langCode: String = "en"): String {
    val dict = langDictionary[langCode]
    val value = dict?.get(key)
    if (!value.isNullOrBlank()) return value

    val enValue = enStrings[key]
    if (!enValue.isNullOrBlank()) return enValue

    return key
  }
}
