package com.example.bookstore.ui.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.bookstore.data.remote.dinero
import com.example.bookstore.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutView(
    viewModel: CarritoViewModel,
    onVolver: () -> Unit,
    onCompraHecha: () -> Unit
) {
    val context = LocalContext.current
    val lineas by viewModel.items.collectAsStateWithLifecycle()
    val estado by viewModel.checkout.collectAsStateWithLifecycle()
    val total = lineas.sumOf { it.precio * it.cantidad }

    LaunchedEffect(Unit) { viewModel.limpiarError() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar compra") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("1. Método de pago", style = MaterialTheme.typography.titleMedium)
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = true, onClick = {})
                    Column {
                        Text("Pago contra entrega", fontWeight = FontWeight.SemiBold)
                        Text("Paga en efectivo al recibir su pedido.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.6f)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = false, onClick = {}, enabled = false)
                    Column {
                        Text("Tarjeta de crédito / débito", fontWeight = FontWeight.SemiBold)
                        Text(
                            "No disponible de momento",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text("2. Resumen", style = MaterialTheme.typography.titleMedium)
            lineas.forEach { linea ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${linea.titulo} × ${linea.cantidad}", modifier = Modifier.weight(1f))
                    Text((linea.precio * linea.cantidad).dinero())
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text(total.dinero(), fontWeight = FontWeight.Bold)
            }

            estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                enabled = lineas.isNotEmpty() && !estado.enviando,
                onClick = {
                    viewModel.confirmarCompra {
                        Toast.makeText(context, "¡Su compra se realizó con éxito!", Toast.LENGTH_LONG).show()
                        onCompraHecha()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (estado.enviando) "Procesando..." else "Confirmar") }
        }
    }
}