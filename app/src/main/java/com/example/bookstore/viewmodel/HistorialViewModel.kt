package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.remote.VentaDto
import com.example.bookstore.repository.BookstoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistorialUiState(
    val cargando: Boolean = false,
    val compras: List<VentaDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val repo: BookstoreRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(HistorialUiState())
    val estado: StateFlow<HistorialUiState> = _estado.asStateFlow()

    // GET /ventas/mis-compras: las más recientes primero
    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            repo.misCompras()
                .onSuccess { lista ->
                    _estado.update { it.copy(cargando = false, compras = lista.sortedByDescending { v -> v.id }) }
                }
                .onFailure { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    // Al cerrar sesión no deben quedar compras del usuario anterior
    fun reiniciar() {
        _estado.value = HistorialUiState()
    }
}