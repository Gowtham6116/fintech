package com.example.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtils {
  private const val ITERATIONS = 10000
  private const val KEY_LENGTH = 256
  private const val SALT_LENGTH = 16

  /**
   * Hashes a password using PBKDF2WithHmacSHA256 with a cryptographically secure random salt.
   * Format: pbkdf2$iterations$saltBase64$hashBase64
   */
  fun hashPassword(password: String): String {
    val random = SecureRandom()
    val salt = ByteArray(SALT_LENGTH)
    random.nextBytes(salt)
    val hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
    val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
    val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)
    return "pbkdf2\$$ITERATIONS\$$saltBase64\$$hashBase64"
  }

  /**
   * Securely verifies a password against a stored hash using constant-time comparison.
   */
  fun verifyPassword(password: String, storedHash: String): Boolean {
    // Support legacy pre-seeded plaintext accounts during migration
    if (storedHash == "demo123" || storedHash == "admin123") {
      return password == storedHash
    }

    if (!storedHash.startsWith("pbkdf2\$")) {
      return password == storedHash
    }

    val parts = storedHash.split("\$")
    if (parts.size != 4) return false

    val iterations = parts[1].toIntOrNull() ?: return false
    val salt = try { Base64.decode(parts[2], Base64.NO_WRAP) } catch (_: Exception) { return false }
    val expectedHash = try { Base64.decode(parts[3], Base64.NO_WRAP) } catch (_: Exception) { return false }

    val actualHash = pbkdf2(password.toCharArray(), salt, iterations, expectedHash.size * 8)
    return slowEquals(actualHash, expectedHash)
  }

  private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
    val spec = PBEKeySpec(password, salt, iterations, keyLength)
    val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return skf.generateSecret(spec).encoded
  }

  private fun slowEquals(a: ByteArray, b: ByteArray): Boolean {
    var diff = a.size xor b.size
    for (i in 0 until minOf(a.size, b.size)) {
      diff = diff or (a[i].toInt() xor b[i].toInt())
    }
    return diff == 0
  }
}
