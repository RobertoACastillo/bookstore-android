package com.example.bookstore.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookstoreApi {

    @POST("auth/login")
    suspend fun login(@Body datos: LoginRequest): AuthResponse

    @GET("libros")
    suspend fun libros(): List<LibroDto>

    @GET("imagenes/libro/{id}")
    suspend fun imagenes(@Path("id") libroId: Int): List<LibroImagenDto>

    @POST("ventas")
    suspend fun registrarVenta(@Body venta: VentaRequest): VentaDto

    @GET("ventas/mis-compras")
    suspend fun misCompras(): List<VentaDto>
}