package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.Sesion
import com.example.bookstore.repository.BookstoreRepository
import com.example.bookstore.repository.SesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EstadoSesion {
    data object Cargando : EstadoSesion                      // se está leyendo el DataStore
    data object SinSesion : EstadoSesion                     // se muestra el login
    data class ConSesion(val sesion: Sesion) : EstadoSesion  // se muestra la tienda
}

data class LoginUiState(val cargando: Boolean = false, val error: String? = null)

@HiltViewModel
class SesionViewModel @Inject constructor(
    private val repo: BookstoreRepository,
    private val sesionRepo: SesionRepository
) : ViewModel() {

    // Se deriva del DataStore: guardar o borrar la sesión cambia la pantalla sin navegar a mano
    val estado: StateFlow<EstadoSesion> = sesionRepo.sesion
        .map { sesion ->
            if (sesion != null && sesion.estaVigente) EstadoSesion.ConSesion(sesion)
            else EstadoSesion.SinSesion
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, EstadoSesion.Cargando)

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    fun iniciarSesion(email: String, clave: String) {
        if (_login.value.cargando) return                    // ignora toques repetidos
        viewModelScope.launch {
            _login.value = LoginUiState(cargando = true)
            repo.iniciarSesion(email, clave)
                .onSuccess { _login.value = LoginUiState() }
                .onFailure { _login.value = LoginUiState(error = it.message) }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch { sesionRepo.cerrar() }
    }
}