package com.example.bookstore.data.remote

object ApiConfig {
    // Con adb reverse, localhost del teléfono llega a la PC (en emulador sin adb: http://10.0.2.2:8080/)
    private const val SERVIDOR = "http://localhost:8080/"
    const val BASE_URL = SERVIDOR + "api/"      // debe terminar en /

    // "abc.jpg" -> "http://localhost:8080/images/abc.jpg"
    fun urlFoto(nombreArchivo: String?): String? = nombreArchivo?.let { SERVIDOR + "images/" + it }
}