package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.Locale
import kotlin.math.*

data class UserCoordinates(
  val latitude: Double,
  val longitude: Double,
  val isGpsActive: Boolean,
  val locationName: String = "Current Location",
  val source: String = "GPS" // "GPS", "ADDRESS", "DEFAULT"
)

object LocationService {
  val DEFAULT_LOCATION = UserCoordinates(
    latitude = 13.0827,
    longitude = 80.2707,
    isGpsActive = false,
    locationName = "Chennai, Tamil Nadu",
    source = "DEFAULT"
  )

  // Predefined known Indian city coordinates for instant, offline-first geocoding
  private val cityCoordinates = mapOf(
    "chennai" to Pair(13.0827, 80.2707),
    "madurai" to Pair(9.9252, 78.1198),
    "coimbatore" to Pair(11.0168, 76.9558),
    "trichy" to Pair(10.7905, 78.7047),
    "tiruchirappalli" to Pair(10.7905, 78.7047),
    "salem" to Pair(11.6643, 78.1460),
    "tirunelveli" to Pair(8.7139, 77.7567),
    "vellore" to Pair(12.9165, 79.1325),
    "bengaluru" to Pair(12.9716, 77.5946),
    "bangalore" to Pair(12.9716, 77.5946),
    "hyderabad" to Pair(17.3850, 78.4867),
    "mumbai" to Pair(19.0760, 72.8777),
    "delhi" to Pair(28.6139, 77.2090),
    "new delhi" to Pair(28.6139, 77.2090),
    "pune" to Pair(18.5204, 73.8567),
    "ahmedabad" to Pair(23.0225, 72.5714),
    "kolkata" to Pair(22.5726, 88.3639),
    "jaipur" to Pair(26.9124, 75.7873),
    "lucknow" to Pair(26.8467, 80.9462),
    "kochi" to Pair(9.9312, 76.2673),
    "cochin" to Pair(9.9312, 76.2673),
    "thiruvananthapuram" to Pair(8.5241, 76.9366),
    "chandigarh" to Pair(30.7333, 76.7794),
    "guwahati" to Pair(26.1445, 91.7362),
    "bhubaneswar" to Pair(20.2961, 85.8245),
    "patna" to Pair(25.5941, 85.1376)
  )

  @SuppressLint("MissingPermission")
  fun getCurrentLocation(context: Context): UserCoordinates? {
    return try {
      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
      val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
      val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

      var bestLocation: Location? = null
      if (isGpsEnabled) {
        bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
      }
      if (bestLocation == null && isNetworkEnabled) {
        bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
      }

      if (bestLocation != null) {
        UserCoordinates(
          latitude = bestLocation.latitude,
          longitude = bestLocation.longitude,
          isGpsActive = true,
          locationName = "Device GPS (${String.format(Locale.US, "%.4f", bestLocation.latitude)}, ${String.format(Locale.US, "%.4f", bestLocation.longitude)})",
          source = "GPS"
        )
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Geocodes an address string into latitude/longitude.
   * Priority:
   * 1. Check known Indian city coordinates
   * 2. Android Geocoder API
   * 3. Default fallback
   */
  fun geocodeAddress(context: Context, address: String, city: String, state: String = ""): UserCoordinates {
    val cleanCity = city.trim().lowercase()
    val cleanAddr = address.trim().lowercase()

    // 1. Direct city dictionary lookup
    for ((cityName, coords) in cityCoordinates) {
      if (cleanCity.contains(cityName) || cleanAddr.contains(cityName)) {
        return UserCoordinates(
          latitude = coords.first,
          longitude = coords.second,
          isGpsActive = false,
          locationName = "${city.ifBlank { address }}, ${state.ifBlank { "India" }}",
          source = "ADDRESS"
        )
      }
    }

    // 2. Android Geocoder fallback
    try {
      if (Geocoder.isPresent()) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val fullQuery = listOf(address, city, state, "India").filter { it.isNotBlank() }.joinToString(", ")
        val results = geocoder.getFromLocationName(fullQuery, 1)
        if (!results.isNullOrEmpty()) {
          val loc = results[0]
          return UserCoordinates(
            latitude = loc.latitude,
            longitude = loc.longitude,
            isGpsActive = false,
            locationName = "${city.ifBlank { address }}, ${state.ifBlank { "India" }}",
            source = "ADDRESS"
          )
        }
      }
    } catch (_: Exception) {}

    // 3. Fallback
    return DEFAULT_LOCATION.copy(
      locationName = if (city.isNotBlank()) "$city (Approx)" else DEFAULT_LOCATION.locationName,
      source = "ADDRESS"
    )
  }

  fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
  }
}
