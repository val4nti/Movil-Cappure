package cl.duoc.cappucare.ui.screens.codigo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.ui.components.BarraSuperior
import cl.duoc.cappucare.utils.Validaciones
import cl.duoc.cappucare.viewmodel.BuscarCodigoViewModel

/**
 * Pantalla para escribir a mano el código de barras con un teclado numérico propio
 * y buscar el producto. Si no existe, ofrece crearlo con ese código.
 */
@Composable
fun BuscarCodigoScreen(
    puedeCrear: Boolean,
    onVolver: () -> Unit,
    onEncontrado: (Long) -> Unit,
    onCrearProducto: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BuscarCodigoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val completo = state.codigo.length == Validaciones.LARGO_CODIGO

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(titulo = "Código de barras", onVolver = onVolver)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Escríbelo a mano para buscar el producto", color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Visor del código: el borde cambia de color al completar los 13 dígitos o si hay error
            val colorBorde by animateColorAsState(
                when {
                    state.error != null -> MaterialTheme.colorScheme.error
                    completo -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                label = "bordeCodigo"
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, colorBorde),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = state.codigo.ifEmpty { "Escribe el código" },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 26.sp,
                        letterSpacing = 2.sp,
                        color = if (state.codigo.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${state.codigo.length} / ${Validaciones.LARGO_CODIGO} dígitos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            state.error?.let { error ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Text(" $error", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            AnimatedVisibility(visible = state.noEncontrado) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("No existe un producto con el código ${state.codigo}", fontWeight = FontWeight.SemiBold)
                        if (puedeCrear) {
                            Button(onClick = { onCrearProducto(state.codigo) }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Crear producto con este código")
                            }
                        }
                    }
                }
            }

            // Teclado numérico
            val filas = listOf(listOf('1', '2', '3'), listOf('4', '5', '6'), listOf('7', '8', '9'))
            filas.forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    fila.forEach { digito ->
                        FilledTonalButton(
                            onClick = { viewModel.onDigito(digito) },
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) { Text(digito.toString(), fontSize = 22.sp) }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = viewModel::onEjemplo, modifier = Modifier.weight(1f).height(56.dp)) {
                    Text("Ejemplo")
                }
                FilledTonalButton(onClick = { viewModel.onDigito('0') }, modifier = Modifier.weight(1f).height(56.dp)) {
                    Text("0", fontSize = 22.sp)
                }
                OutlinedButton(onClick = viewModel::onBorrar, modifier = Modifier.weight(1f).height(56.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Borrar")
                }
            }

            Button(
                onClick = { viewModel.buscar(onEncontrado) },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Buscar producto") }
        }
    }
}
