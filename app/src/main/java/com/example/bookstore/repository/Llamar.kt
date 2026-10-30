package com.example.bookstore.repository

import com.example.bookstore.data.remote.MensajeDto
import com.google.gson.Gson
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException

// Convierte cualquier fallo de red en un mensaje legible dentro de un Result
suspend fun <T> llamar(bloque: suspend () -> T): Result<T> = try {
    Result.success(bloque())
} catch (e: HttpException) {
    val mensaje = when (e.code()) {
        401 -> "Su sesión expiró. Inicie sesión de nuevo."
        else -> mensajeDelServidor(e) ?: "Error del servidor (${e.code()})."
    }
    Result.failure(Exception(mensaje))
} catch (e: IOException) {
    Result.failure(Exception("No se pudo conectar con el servidor. Revise adb reverse tcp:8080 y que la API esté activa."))
} catch (e: JsonParseException) {
    Result.failure(Exception("La respuesta del servidor no tiene el formato esperado."))
}

// La API devuelve los errores como {"message": "..."}
private fun mensajeDelServidor(error: HttpException): String? = try {
    val cuerpo = error.response()?.errorBody()?.string()
    Gson().fromJson(cuerpo, MensajeDto::class.java)?.message
} catch (e: Exception) {
    null
}