package com.example.bookstore.utils

import android.util.Base64
import org.json.JSONObject

// Solo LEE el contenido del token; la firma la verifica el servidor
object JwtUtil {

    private fun datos(token: String): JSONObject? = try {
        val bytes = Base64.decode(token.split(".")[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        JSONObject(String(bytes))
    } catch (e: Exception) {
        null
    }

    // La respuesta del login no trae el id, pero el token sí (claim "id")
    fun id(token: String): Int = datos(token)?.optInt("id", 0) ?: 0

    // Fecha de vencimiento en segundos (claim "exp"); 0 si no se pudo leer
    fun expiracion(token: String): Long = datos(token)?.optLong("exp", 0L) ?: 0L
}