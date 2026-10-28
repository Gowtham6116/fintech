package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.i18n.LanguageManager
import com.example.location.LocationService
import com.example.voice.VoiceExpenseParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FinGuide", appName)
  }

  @Test
  fun `test voice expense parsing for lunch expense`() {
    val spoken = "I spent 250 rupees for lunch today"
    val parsed = VoiceExpenseParser.parseSpokenText(spoken)
    assertNotNull(parsed)
    assertEquals(250.0, parsed!!.amount, 0.01)
    assertEquals("Food", parsed.category)
  }

  @Test
  fun `test 15 languages support in LanguageManager`() {
    assertEquals(15, LanguageManager.supportedLanguages.size)
    val tamilApp = LanguageManager.getString("app_name", "ta")
    assertTrue(tamilApp.isNotBlank())
    val hindiApp = LanguageManager.getString("app_name", "hi")
    assertTrue(hindiApp.isNotBlank())
  }

  @Test
  fun `test haversine distance calculation`() {
    // Distance between Chennai (13.0827, 80.2707) and Guindy (13.0118, 80.2074)
    val distance = LocationService.calculateDistanceKm(13.0827, 80.2707, 13.0118, 80.2074)
    assertTrue(distance in 9.0..12.0)
  }

  @Test
  fun `test user profile updates for student and working professional roles`() {
    val studentUser = com.example.data.model.User(
      id = "user-101",
      name = "Arun Kumar",
      email = "arun@student.ac.in",
      passwordHash = "hash123",
      userType = com.example.data.model.UserType.STUDENT.name,
      address = "Hostel 4, IIT Madras",
      city = "Chennai",
      state = "Tamil Nadu",
      postalCode = "600036"
    )
    assertEquals("STUDENT", studentUser.userType)
    assertEquals("Hostel 4, IIT Madras", studentUser.residentialAddress)

    val proUser = studentUser.copy(
      userType = com.example.data.model.UserType.WORKING_PROFESSIONAL.name,
      address = "42 Residency Road",
      city = "Bengaluru",
      state = "Karnataka",
      postalCode = "560025"
    )
    assertEquals("WORKING_PROFESSIONAL", proUser.userType)
    assertEquals("Bengaluru", proUser.city)
    assertEquals("560025", proUser.postalCode)
  }

  @Test
  fun `test sync state lifecycle transitions`() {
    val idleState: com.example.data.sync.SyncState = com.example.data.sync.SyncState.Idle
    val syncingState: com.example.data.sync.SyncState = com.example.data.sync.SyncState.Syncing("Pushing to Firestore...")
    val result = com.example.data.sync.SyncResult(success = true, transactionsSynced = 5, message = "Synced 5 records")
    val successState: com.example.data.sync.SyncState = com.example.data.sync.SyncState.Success(result)

    assertNotNull(idleState)
    assertNotNull(syncingState)
    assertNotNull(successState)
    assertEquals(5, (successState as com.example.data.sync.SyncState.Success).result.transactionsSynced)
  }
}
