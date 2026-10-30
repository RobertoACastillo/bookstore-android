package com.example.bookstore.data.remote

import java.util.Locale

// ---- Login ----
data class LoginRequest(val email: String, val password: String)
data class AuthDto(val token: String, val email: String, val nombre: String, val rol: String)
data class AuthResponse(val message: String?, val auth: AuthDto?)
data class MensajeDto(val message: String?)

// ---- Libros ----
data class AutorDto(val id: Int?, val nombre: String?)
data class LibroImagenDto(val id: Int, val nombreArchivo: String)

data class LibroDto(
    val id: Int,
    val titulo: String,
    val precio: Double,
    val stock: Int,
    val descripcion: String?,
    val autores: List<AutorDto>?,
    val foto: String? = null          // no viene en el JSON: se rellena con /imagenes/libro/{id}
)

// ---- Ventas ----
data class DetalleVentaDto(val libroId: Int, val cantidad: Int)
data class VentaRequest(val usuarioId: Int, val detalles: List<DetalleVentaDto>)
data class VentaDto(val id: Int, val fecha: String?, val total: Double?, val estado: String?)

// ---- Ayudas de formato ----
fun LibroDto.autoresTexto(): String =
    autores.orEmpty().mapNotNull { it.nombre }.joinToString(", ").ifBlank { "Autor desconocido" }

fun Double.dinero(): String = "$" + "%.2f".format(Locale.US, this)