package com.example.bookstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookstore.navigation.NavManager
import com.example.bookstore.viewmodel.CarritoViewModel
import com.example.bookstore.viewmodel.HistorialViewModel
import com.example.bookstore.viewmodel.LibrosViewModel
import com.example.bookstore.viewmodel.SesionViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.bookstore.ui.theme.BookstoreandroidTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookstoreandroidTheme {   // use el nombre del tema que generó la plantilla
                val sesionVM: SesionViewModel = viewModel()
                val librosVM: LibrosViewModel = viewModel()
                val carritoVM: CarritoViewModel = viewModel()
                val historialVM: HistorialViewModel = viewModel()
                NavManager(sesionVM, librosVM, carritoVM, historialVM)
            }
        }
    }
}