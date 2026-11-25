package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.KaamConstants
import com.example.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.kaamDataStore: DataStore<Preferences> by preferencesDataStore(name = "kaam_connect_prefs")

class SessionStore(private val context: Context) {

  private object Keys {
    val FULL_NAME = stringPreferencesKey("user_full_name")
    val PHONE = stringPreferencesKey("user_phone")
    val PREFERRED_STATE = stringPreferencesKey("preferred_state")
    val PREFERRED_CITY = stringPreferencesKey("preferred_city")
    val SKIPPED_REQUESTS = stringSetPreferencesKey("skipped_request_ids")
    val REPORTED_REQUESTS = stringSetPreferencesKey("reported_request_ids")
  }

  val sessionFlow: Flow<UserSession> =
    context.kaamDataStore.data.map { prefs ->
      UserSession(
        fullName = prefs[Keys.FULL_NAME].orEmpty(),
        phone = prefs[Keys.PHONE].orEmpty(),
        preferredState = prefs[Keys.PREFERRED_STATE] ?: KaamConstants.DEFAULT_STATE,
        preferredCity = prefs[Keys.PREFERRED_CITY] ?: KaamConstants.DEFAULT_CITY,
      )
    }

  val skippedRequestIdsFlow: Flow<Set<String>> =
    context.kaamDataStore.data.map { prefs -> prefs[Keys.SKIPPED_REQUESTS] ?: emptySet() }

  val reportedRequestIdsFlow: Flow<Set<String>> =
    context.kaamDataStore.data.map { prefs -> prefs[Keys.REPORTED_REQUESTS] ?: emptySet() }

  suspend fun saveLogin(fullName: String, phone: String) {
    val sanitizedPhone = phone.filter { it.isDigit() }.takeLast(10)
    context.kaamDataStore.edit { prefs ->
      prefs[Keys.FULL_NAME] = fullName.trim()
      prefs[Keys.PHONE] = sanitizedPhone
    }
  }

  suspend fun savePreferredLocation(state: String, city: String) {
    context.kaamDataStore.edit { prefs ->
      if (state.isNotBlank()) prefs[Keys.PREFERRED_STATE] = state.trim()
      if (city.isNotBlank()) prefs[Keys.PREFERRED_CITY] = city.trim()
    }
  }

  suspend fun markRequestSkipped(requestId: String) {
    context.kaamDataStore.edit { prefs ->
      val current = prefs[Keys.SKIPPED_REQUESTS] ?: emptySet()
      prefs[Keys.SKIPPED_REQUESTS] = current + requestId
    }
  }

  suspend fun markRequestReported(requestId: String) {
    context.kaamDataStore.edit { prefs ->
      val current = prefs[Keys.REPORTED_REQUESTS] ?: emptySet()
      prefs[Keys.REPORTED_REQUESTS] = current + requestId
    }
  }

  suspend fun logout() {
    context.kaamDataStore.edit { prefs ->
      prefs.remove(Keys.FULL_NAME)
      prefs.remove(Keys.PHONE)
    }
  }
}
