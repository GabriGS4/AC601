package com.gabrielsanchez.ac601.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PersonaViewModel(private val context: Context) : ViewModel() {

    val Context.dataStore by preferencesDataStore(name = "Settings")

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    fun setEmail(email: String) {
        _email.value = email
    }

    init {
        // Cargar datos al inicializar el ViewModel
        viewModelScope.launch {
            loadDataFromDataStore()
        }
    }

    fun isEmailValidAndNotEmpty(): Boolean {
        return _email.value?.isNotEmpty() == true && android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()
    }

    private suspend fun loadDataFromDataStore() {
        context.dataStore.data.map { preferences ->
            preferences[EMAIL_KEY] ?: ""
        }.collect { emailValue ->
            _email.postValue(emailValue)
        }
    }


    suspend fun saveDataToDataStore() {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email.value ?: ""
        }
    }

    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
    }
}
