package cl.duoc.cappucare.ui.screens.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.data.local.entity.MovimientoConProducto
import cl.duoc.cappucare.model.FiltroHistorial
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.ui.components.BarraSuperior
import cl.duoc.cappucare.ui.theme.AgotadoTexto
import cl.duoc.cappucare.ui.theme.DisponibleTexto
import cl.duoc.cappucare.utils.comoFechaCorta
import cl.duoc.cappucare.viewmodel.HistorialViewModel

/**
 * Historial de ingresos y salidas, del más reciente al más antiguo, con filtro por tipo.
 *
 * @param onVolver null cuando se abre desde la barra inferior.
 */
@Composable
fun HistorialScreen(
    onVolver: (() -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: HistorialViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = state.nombreProducto?.let { "Historial · $it" } ?: "Historial",
            onVolver = onVolver
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroHistorial.entries.forEach { filtro ->
                FilterChip(
                    selected = state.filtro == filtro,
                    onClick = { viewModel.onFiltroChanged(filtro) },
                    label = { Text(filtro.etiqueta) }
                )
            }
        }

        Box(Modifier.fillMaxSize()) {
            when {
                state.cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.movimientos.isEmpty() -> Text(
                    "No hay movimientos",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                else -> LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.movimientos, key = { it.movimiento.id }) { item ->
                        FilaMovimiento(item, Modifier.animateItem())
                    }
                }
            }
        }
    }
}

/** Una fila del historial: producto, motivo y fecha a la izquierda; cantidad con signo a la derecha. */
@Composable
private fun FilaMovimiento(item: MovimientoConProducto, modifier: Modifier = Modifier) {
    val esIngreso = item.movimiento.tipo == TipoMovimiento.INGRESO
    val color = if (esIngreso) DisponibleTexto else AgotadoTexto

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.producto.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    "${item.movimiento.motivo} · ${item.movimiento.fecha.comoFechaCorta()} · ${item.movimiento.perfil}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (esIngreso) "+" else "−") + item.movimiento.cantidad,
                    color = color,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(item.movimiento.tipo.etiqueta, color = color, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
