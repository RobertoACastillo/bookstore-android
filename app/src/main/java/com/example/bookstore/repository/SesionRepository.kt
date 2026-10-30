package com.example.bookstore.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.bookstore.data.ClavesSesion
import com.example.bookstore.data.Sesion
import com.example.bookstore.data.remote.AuthDto
import com.example.bookstore.data.sesionDataStore
import com.example.bookstore.utils.JwtUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SesionRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // null = no hay sesión guardada
    val sesion: Flow<Sesion?> = context.sesionDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { p ->
            val token = p[ClavesSesion.TOKEN] ?: return@map null
            Sesion(token, p[ClavesSesion.ID] ?: 0, p[ClavesSesion.EXPIRA_EN] ?: 0L)
        }

    suspend fun guardar(auth: AuthDto) {
        context.sesionDataStore.edit { p ->
            p[ClavesSesion.TOKEN] = auth.token
            p[ClavesSesion.ID] = JwtUtil.id(auth.token)
            p[ClavesSesion.EXPIRA_EN] = JwtUtil.expiracion(auth.token)
        }
    }

    suspend fun cerrar() {
        context.sesionDataStore.edit { it.clear() }
    }

    // Solo para el interceptor de OkHttp (hilo de red, no es una corrutina)
    fun sesionActual(): Sesion? = runBlocking { sesion.first() }
}