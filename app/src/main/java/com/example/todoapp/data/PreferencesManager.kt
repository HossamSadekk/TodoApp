package com.example.todoapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_setting")

data class FilterPreferences(val sort: Sort, val hideCompleted: Boolean)

private const val TAG = "PreferencesManager"

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val settingsDataStore = context.dataStore

    val preferencesFlow = settingsDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException if it can't read the data
            if (exception is IOException) {
                emit(emptyPreferences())
                Log.e(TAG, "error:", exception)
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = Sort.valueOf(preferences[keys.SORT_ORDER] ?: Sort.BY_DATE.name)
            val hideCompleted = preferences[keys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sort: Sort) {
        settingsDataStore.edit { settings ->
            settings[keys.SORT_ORDER] = sort.name
        }
    }

    suspend fun hideCompleted(hide: Boolean) {
        settingsDataStore.edit { settings ->
            settings[keys.HIDE_COMPLETED] = hide
        }
    }

    private object keys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }
}


enum class Sort { BY_NAME, BY_DATE }