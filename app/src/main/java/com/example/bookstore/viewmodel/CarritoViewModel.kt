package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.remote.LibroDto
import com.example.bookstore.repository.BookstoreRepository
import com.example.bookstore.repository.CarritoRepository
import com.example.bookstore.repository.ItemCarrito
import com.example.bookstore.repository.SesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(val enviando: Boolean = false, val error: String? = null)

@HiltViewModel
class CarritoViewModel @Inject constructor(
    private val carrito: CarritoRepository,
    private val repo: BookstoreRepository,
    private val sesionRepo: SesionRepository
) : ViewModel() {

    val items: StateFlow<List<ItemCarrito>> = carrito.items

    private val _checkout = MutableStateFlow(CheckoutUiState())
    val checkout: StateFlow<CheckoutUiState> = _checkout.asStateFlow()

    fun agregar(libro: LibroDto) = carrito.agregar(libro)
    fun cambiarCantidad(libroId: Int, delta: Int) = carrito.cambiarCantidad(libroId, delta)
    fun quitar(libroId: Int) = carrito.quitar(libroId)

    // POST /ventas
    fun confirmarCompra(alTerminar: () -> Unit) {
        if (_checkout.value.enviando) return
        viewModelScope.launch {
            val usuarioId = sesionRepo.sesion.first()?.id ?: return@launch
            _checkout.update { it.copy(enviando = true, error = null) }
            repo.comprar(usuarioId, carrito.items.value)
                .onSuccess {
                    carrito.vaciar()
                    _checkout.update { it.copy(enviando = false) }
                    alTerminar()
                }
                .onFailure { e -> _checkout.update { it.copy(enviando = false, error = e.message) } }
        }
    }

    fun limpiarError() { _checkout.update { it.copy(error = null) } }

    // Al cerrar sesión no deben quedar datos del usuario anterior
    fun reiniciar() {
        carrito.vaciar()
        _checkout.value = CheckoutUiState()
    }
}