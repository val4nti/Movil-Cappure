package cl.duoc.cappucare.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.cappucare.model.EstadoStock
import cl.duoc.cappucare.ui.theme.AgotadoFondo
import cl.duoc.cappucare.ui.theme.AgotadoTexto
import cl.duoc.cappucare.ui.theme.DisponibleFondo
import cl.duoc.cappucare.ui.theme.DisponibleTexto
import cl.duoc.cappucare.ui.theme.StockBajoFondo
import cl.duoc.cappucare.ui.theme.StockBajoTexto

/**
 * Etiqueta de color que muestra el estado del stock (Disponible, Stock bajo, Agotado).
 *
 * El color se anima con [animateColorAsState]: cuando el stock cambia de estado
 * (por ejemplo, tras registrar una venta), la etiqueta pasa suavemente al nuevo color.
 */
@Composable
fun EstadoChip(
    estado: EstadoStock,
    modifier: Modifier = Modifier
) {
    val (fondoObjetivo, textoObjetivo) = when (estado) {
        EstadoStock.DISPONIBLE -> DisponibleFondo to DisponibleTexto
        EstadoStock.STOCK_BAJO -> StockBajoFondo to StockBajoTexto
        EstadoStock.AGOTADO -> AgotadoFondo to AgotadoTexto
    }
    val fondo by animateColorAsState(fondoObjetivo, label = "fondoEstado")
    val texto by animateColorAsState(textoObjetivo, label = "textoEstado")

    Text(
        text = estado.etiqueta,
        color = texto,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(fondo, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Preview
@Composable
private fun EstadoChipPreview() {
    EstadoChip(EstadoStock.STOCK_BAJO)
}
