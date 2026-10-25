package cl.duoc.cappucare.ui.screens.inventario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.model.FiltroInventario
import cl.duoc.cappucare.ui.components.AvisoStockBajo
import cl.duoc.cappucare.ui.components.ProductoItem
import cl.duoc.cappucare.viewmodel.InventarioViewModel

/**
 * Pantalla principal: listado de productos con buscador, filtros y aviso de stock bajo.
 *
 * La pantalla no guarda estado propio: todo viene de [InventarioViewModel.uiState]
 * y cada interacción se envía al ViewModel (flujo unidireccional de datos).
 *
 * @param usuario Nombre del usuario conectado, para el saludo.
 * @param puedeCrear true si el perfil puede crear productos (solo Encargado de bodega).
 * @param onProductoClick Se llama con el id del producto tocado, para abrir su detalle.
 * @param onNuevoProducto Se llama al tocar "+".
 * @param onAlertas Se llama al tocar la campana.
 * @param onBuscarCodigo Se llama al tocar el ícono de código de barras del buscador.
 * @param onCerrarSesion Se llama al tocar el ícono de salir.
 */
@Composable
fun InventarioScreen(
    modifier: Modifier = Modifier,
    viewModel: InventarioViewModel = viewModel(),
    usuario: String = "",
    puedeCrear: Boolean = true,
    onProductoClick: (Long) -> Unit = {},
    onNuevoProducto: () -> Unit = {},
    onAlertas: () -> Unit = {},
    onBuscarCodigo: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Encabezado: marca, título y acciones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (usuario.isBlank()) "Cappucare" else "Cappucare · Hola, $usuario",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Inventario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            // El Vendedor no ve el botón para crear productos (regla de negocio por perfil)
            if (puedeCrear) {
                FilledIconButton(onClick = onNuevoProducto) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo producto")
                }
            }
            IconButton(onClick = onAlertas) {
                BadgedBox(
                    badge = {
                        if (state.totalStockBajo > 0) {
                            Badge { Text(state.totalStockBajo.toString()) }
                        }
                    }
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Alertas de stock")
                }
            }
            IconButton(onClick = onCerrarSesion) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
            }
        }

        // Buscador por nombre o código
        OutlinedTextField(
            value = state.busqueda,
            onValueChange = viewModel::onBusquedaChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por nombre o código") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (state.busqueda.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onBusquedaChanged("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                    }
                } else {
                    IconButton(onClick = onBuscarCodigo) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Buscar por código de barras")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        // El aviso aparece y desaparece con animación según haya o no productos bajo el mínimo
        AnimatedVisibility(
            visible = state.totalStockBajo > 0,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AvisoStockBajo(
                cantidad = state.totalStockBajo,
                onVer = { viewModel.onFiltroChanged(FiltroInventario.STOCK_BAJO) }
            )
        }

        // Filtros
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.filtro == FiltroInventario.TODOS,
                onClick = { viewModel.onFiltroChanged(FiltroInventario.TODOS) },
                label = { Text("Todos · ${state.totalProductos}") }
            )
            FilterChip(
                selected = state.filtro == FiltroInventario.STOCK_BAJO,
                onClick = { viewModel.onFiltroChanged(FiltroInventario.STOCK_BAJO) },
                label = { Text("Stock bajo · ${state.totalStockBajo}") }
            )
        }

        // Contenido: cargando, lista vacía o lista de productos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                state.cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.productos.isEmpty() -> Text(
                    text = "No hay productos que coincidan",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.productos,
                        key = { it.id }
                    ) { producto ->
                        ProductoItem(
                            producto = producto,
                            onClick = { onProductoClick(producto.id) },
                            // Anima la entrada, salida y reordenamiento al filtrar o buscar
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
