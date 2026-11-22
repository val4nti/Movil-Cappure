package cl.duoc.cappucare.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.utils.comoPesos
import cl.duoc.cappucare.viewmodel.IndicadoresViewModel

/**
 * Tarjeta con el dólar y la UF del día, obtenidos de la API externa mindicador.cl.
 * Sirve de referencia para los precios de los insumos importados.
 */
@Composable
fun IndicadoresCard(
    modifier: Modifier = Modifier,
    viewModel: IndicadoresViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.AttachMoney, contentDescription = null)
            Spacer(Modifier.width(8.dp))

            val indicadores = state.indicadores
            when {
                state.cargando -> {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Consultando dólar y UF…", modifier = Modifier.weight(1f))
                }

                indicadores != null -> Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dólar ${indicadores.dolar.comoPesos()} · UF ${indicadores.uf.comoPesos()}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "mindicador.cl · ${indicadores.fecha}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    Text(
                        text = state.error ?: "Sin datos",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::cargar) { Text("Reintentar") }
                }
            }
        }
    }
}
