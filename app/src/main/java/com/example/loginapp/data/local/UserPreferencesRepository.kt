package com.example.loginapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val rememberedCountryIsoCode: String?,
    val rememberedPhoneNumber: String?
)

class UserPreferencesRepository(context: Context) {

    private val appContext = context.applicationContext

    object PreferencesKeys {
        val REMEMBERED_COUNTRY_ISO_CODE = stringPreferencesKey("remembered_country_iso_code")
        val REMEMBERED_PHONE_NUMBER = stringPreferencesKey("remembered_phone_number")
    }

    val userPreferencesFlow: Flow<UserPreferences> = appContext.dataStore.data
        .map { preferences ->
            val countryIsoCode = preferences[PreferencesKeys.REMEMBERED_COUNTRY_ISO_CODE]
            val phoneNumber = preferences[PreferencesKeys.REMEMBERED_PHONE_NUMBER]
            UserPreferences(countryIsoCode, phoneNumber)
        }

    suspend fun saveLoginDetails(countryIsoCode: String, phoneNumber: String) {
        appContext.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMEMBERED_COUNTRY_ISO_CODE] = countryIsoCode
            preferences[PreferencesKeys.REMEMBERED_PHONE_NUMBER] = phoneNumber
        }
    }

    suspend fun clearLoginDetails() {
        appContext.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.REMEMBERED_COUNTRY_ISO_CODE)
            preferences.remove(PreferencesKeys.REMEMBERED_PHONE_NUMBER)
        }
    }
}
