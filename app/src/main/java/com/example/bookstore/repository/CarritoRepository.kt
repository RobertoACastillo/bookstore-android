package com.example.bookstore.repository

import com.example.bookstore.data.remote.LibroDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class ItemCarrito(
    val libroId: Int,
    val titulo: String,
    val precio: Double,
    val stock: Int,
    val cantidad: Int
)

// El carrito vive en memoria: se pierde al cerrar la app
@Singleton
class CarritoRepository @Inject constructor() {

    private val _items = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val items: StateFlow<List<ItemCarrito>> = _items.asStateFlow()

    fun agregar(libro: LibroDto) {
        _items.update { lista ->
            if (lista.any { it.libroId == libro.id }) {
                lista.map {
                    if (it.libroId == libro.id) it.copy(cantidad = (it.cantidad + 1).coerceAtMost(it.stock)) else it
                }
            } else {
                lista + ItemCarrito(libro.id, libro.titulo, libro.precio, libro.stock, 1)
            }
        }
    }

    // delta = +1 o -1; si la cantidad llega a 0, el libro sale del carrito
    fun cambiarCantidad(libroId: Int, delta: Int) {
        _items.update { lista ->
            lista.map {
                if (it.libroId == libroId) it.copy(cantidad = (it.cantidad + delta).coerceAtMost(it.stock)) else it
            }.filter { it.cantidad > 0 }
        }
    }

    fun quitar(libroId: Int) {
        _items.update { lista -> lista.filter { it.libroId != libroId } }
    }

    fun vaciar() {
        _items.value = emptyList()
    }
}