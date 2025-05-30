package com.dicoding.intermediate_satu.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(private val context: Context) {
    companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val NAME_KEY = stringPreferencesKey("name")
        val TOKEN_KEY = stringPreferencesKey("token")
        val LATITUDE_KEY = floatPreferencesKey("latitude")
        val LONGITUDE_KEY = floatPreferencesKey("longitude")
    }

    private val userDataFlow = context.dataStore

    val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val nameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[NAME_KEY]
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val locationFlow: Flow<LatLng?> = context.dataStore.data.map { preferences ->
        if (preferences[LATITUDE_KEY] != null || preferences[LONGITUDE_KEY] != null) {
            val latitude = preferences[LATITUDE_KEY]!!.toDouble()
            val longitude = preferences[LONGITUDE_KEY]!!.toDouble()
            val location = LatLng(latitude, longitude)
            location
        } else {
            null
        }
    }

    fun saveUserData(userId: String, name: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataFlow.edit { preferences ->
                preferences[USER_ID_KEY] = userId
                preferences[NAME_KEY] = name
                preferences[TOKEN_KEY] = token
            }
        }
    }

    fun destroyUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userDataFlow.edit { preferences ->
                    preferences.clear()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getTokenOnce(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[TOKEN_KEY] }
            .first()
    }

    fun deleteUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            userDataFlow.edit { preferences ->
                preferences.remove(USER_ID_KEY)
                preferences.remove(NAME_KEY)
                preferences.remove(TOKEN_KEY)
            }
        }
    }

     fun setLatestLocation(latitude: Float, longitude: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataFlow.edit { preferences ->
                preferences[LATITUDE_KEY] = latitude
                preferences[LONGITUDE_KEY] = longitude
            }
        }
    }

}