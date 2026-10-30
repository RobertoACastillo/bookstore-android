package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.remote.LibroDto
import com.example.bookstore.repository.BookstoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibrosUiState(
    val cargando: Boolean = false,
    val libros: List<LibroDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LibrosViewModel @Inject constructor(
    private val repo: BookstoreRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LibrosUiState())
    val estado: StateFlow<LibrosUiState> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            repo.librosConFoto()
                .onSuccess { lista -> _estado.update { it.copy(cargando = false, libros = lista) } }
                .onFailure { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
        }
    }
}