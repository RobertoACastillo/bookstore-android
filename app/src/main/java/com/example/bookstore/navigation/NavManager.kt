package com.example.bookstore.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.bookstore.data.Sesion
import com.example.bookstore.ui.views.CarritoView
import com.example.bookstore.ui.views.CheckoutView
import com.example.bookstore.ui.views.HistorialView
import com.example.bookstore.ui.views.LibrosView
import com.example.bookstore.ui.views.LoginView
import com.example.bookstore.viewmodel.CarritoViewModel
import com.example.bookstore.viewmodel.EstadoSesion
import com.example.bookstore.viewmodel.HistorialViewModel
import com.example.bookstore.viewmodel.LibrosViewModel
import com.example.bookstore.viewmodel.SesionViewModel

private data class Destino(val ruta: String, val titulo: String, val icono: ImageVector)

private val destinos = listOf(
    Destino("libros", "Libros", Icons.Default.Home),
    Destino("carrito", "Carrito", Icons.Default.ShoppingCart),
    Destino("compras", "Compras", Icons.AutoMirrored.Filled.List)
)

@Composable
fun NavManager(
    sesionVM: SesionViewModel,
    librosVM: LibrosViewModel,
    carritoVM: CarritoViewModel,
    historialVM: HistorialViewModel
) {
    val estadoSesion by sesionVM.estado.collectAsStateWithLifecycle()

    LaunchedEffect(estadoSesion) {
        if (estadoSesion is EstadoSesion.SinSesion) {
            carritoVM.reiniciar()
            historialVM.reiniciar()
        }
    }

    when (val estado = estadoSesion) {
        EstadoSesion.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        EstadoSesion.SinSesion -> LoginView(sesionVM)
        is EstadoSesion.ConSesion -> ContenidoApp(
            sesion = estado.sesion,
            librosVM = librosVM,
            carritoVM = carritoVM,
            historialVM = historialVM,
            onCerrarSesion = sesionVM::cerrarSesion
        )
    }
}

@Composable
private fun ContenidoApp(
    sesion: Sesion,
    librosVM: LibrosViewModel,
    carritoVM: CarritoViewModel,
    historialVM: HistorialViewModel,
    onCerrarSesion: () -> Unit
) {
    LaunchedEffect(sesion.token) { librosVM.cargar() }

    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (destinos.any { it.ruta == rutaActual }) {
                NavigationBar {
                    destinos.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                navController.navigate(destino.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "libros",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("libros") {
                LibrosView(
                    viewModel = librosVM,
                    onAgregar = carritoVM::agregar,
                    onCerrarSesion = onCerrarSesion
                )
            }
            composable("carrito") {
                CarritoView(carritoVM, onComprar = { navController.navigate("checkout") })
            }
            composable("compras") {
                HistorialView(historialVM)
            }
            composable("checkout") {
                CheckoutView(
                    viewModel = carritoVM,
                    onVolver = { navController.popBackStack() },
                    onCompraHecha = {
                        librosVM.cargar()
                        navController.navigate("compras") {
                            popUpTo("libros")
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}