package com.example.bookstore.repository

import com.example.bookstore.data.remote.AuthResponse
import com.example.bookstore.data.remote.BookstoreApi
import com.example.bookstore.data.remote.DetalleVentaDto
import com.example.bookstore.data.remote.LibroDto
import com.example.bookstore.data.remote.LoginRequest
import com.example.bookstore.data.remote.VentaDto
import com.example.bookstore.data.remote.VentaRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BookstoreRepository @Inject constructor(
    private val api: BookstoreApi,
    private val sesionRepo: SesionRepository
) {
    // ---------- Login ----------
    suspend fun iniciarSesion(email: String, clave: String): Result<Unit> =
        llamar { api.login(LoginRequest(email.trim(), clave)) }.mapCatching { guardarSesion(it) }

    private suspend fun guardarSesion(respuesta: AuthResponse) {
        val auth = respuesta.auth ?: throw Exception(respuesta.message ?: "No se pudo iniciar sesión.")
        if (auth.rol != "CLIENTE") throw Exception("Esta aplicación es solo para clientes.")
        sesionRepo.guardar(auth)
    }

    // ---------- Libros ----------
    // Trae los libros y, en paralelo, la primera imagen de cada uno
    suspend fun librosConFoto(): Result<List<LibroDto>> = llamar {
        coroutineScope {
            api.libros().map { libro ->
                async {
                    val foto = try {
                        api.imagenes(libro.id).firstOrNull()?.nombreArchivo
                    } catch (e: IOException) {
                        null
                    } catch (e: HttpException) {
                        null
                    }
                    libro.copy(foto = foto)
                }
            }.awaitAll()
        }
    }

    // ---------- Compras ----------
    // El servidor descuenta el stock y deja la venta en PENDIENTE
    suspend fun comprar(usuarioId: Int, items: List<ItemCarrito>): Result<VentaDto> = llamar {
        api.registrarVenta(
            VentaRequest(usuarioId, items.map { DetalleVentaDto(it.libroId, it.cantidad) })
        )
    }

    suspend fun misCompras(): Result<List<VentaDto>> = llamar { api.misCompras() }
}