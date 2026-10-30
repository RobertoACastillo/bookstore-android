package com.example.bookstore.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

data class Sesion(val token: String, val id: Int, val expiraEn: Long) {
    val estaVigente: Boolean
        get() = expiraEn == 0L || System.currentTimeMillis() / 1000 < expiraEn
}

// Una sola instancia por archivo, a nivel superior
val Context.sesionDataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion")

object ClavesSesion {
    val TOKEN = stringPreferencesKey("token")
    val ID = intPreferencesKey("id")
    val EXPIRA_EN = longPreferencesKey("expira_en")
}