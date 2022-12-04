package com.nerds.m_learning.student.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nerds.m_learning.common_ui.signIn.DataOfUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRepository private constructor(context: Context) {

    companion object {
        const val PREFERENCES_NAME = "my_preferences"
        const val PREFERENCES_NAME2 = "my_preferences2"
        private const val TAG = "DataStoreRepository"

        private var instance: DataStoreRepository? = null

        fun getInstance(context: Context): DataStoreRepository {
            if (instance == null) {
                instance = DataStoreRepository(context)
            }
            return instance!!
        }

    }

    private object PreferenceKeys {
        val emailKEY = stringPreferencesKey("USER_EMAIL")
        val passwordKEY = stringPreferencesKey("USER_PASSWORD")
        val radioButtonKEY = stringPreferencesKey("USER_ACCOUNT_TYPE")
        val firstLoginKEY = stringPreferencesKey("FIRST_LOGIN")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
    private val Context.dataStore2: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME2)
    private val mDataStore: DataStore<Preferences> = context.dataStore
    private val mDataStore2: DataStore<Preferences> = context.dataStore2

    suspend fun saveToDataStore(user: DataOfUser) {
        mDataStore.edit { preference ->
            preference[PreferenceKeys.emailKEY] = user.email
            preference[PreferenceKeys.passwordKEY] = user.password
            preference[PreferenceKeys.radioButtonKEY] = user.rbID
        }
    }

    suspend fun saveToDataStore2(firstLogin: String) {
        mDataStore2.edit { preference ->
            preference[PreferenceKeys.firstLoginKEY] = firstLogin
        }
    }

    val readFromDataStore: Flow<DataOfUser> = mDataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.d(TAG, exception.message.toString())
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {

        DataOfUser(
            email = it[PreferenceKeys.emailKEY] ?: "",
            password = it[PreferenceKeys.passwordKEY] ?: "",
            rbID = it[PreferenceKeys.radioButtonKEY] ?: ""

        )
    }

    val readFromDataStore2: Flow<String> = mDataStore2.data.catch { exception ->
        if (exception is IOException) {
            Log.d(TAG, exception.message.toString())
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[PreferenceKeys.firstLoginKEY] ?: ""
    }

}