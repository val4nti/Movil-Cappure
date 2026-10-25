package cl.duoc.cappucare.ui.screens.movimiento

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.model.motivosPara
import cl.duoc.cappucare.ui.components.BarraSuperior
import cl.duoc.cappucare.ui.components.CampoTexto
import cl.duoc.cappucare.ui.components.MensajeExito
import cl.duoc.cappucare.ui.components.SelectorDesplegable
import cl.duoc.cappucare.ui.components.SelectorOpciones
import cl.duoc.cappucare.ui.theme.DisponibleFondo
import cl.duoc.cappucare.ui.theme.DisponibleTexto
import cl.duoc.cappucare.ui.theme.StockBajoFondo
import cl.duoc.cappucare.ui.theme.StockBajoTexto
import cl.duoc.cappucare.utils.comoFecha
import cl.duoc.cappucare.viewmodel.MovimientoViewModel

/**
 * Formulario para registrar un ingreso o una salida de stock.
 * Mientras se escribe la cantidad, se muestra en vivo el stock que quedará.
 *
 * @param onVolver null cuando se abre desde la barra inferior (no hay a dónde volver).
 */
@Composable
fun MovimientoScreen(
    perfil: Perfil,
    onVolver: (() -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: MovimientoViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val form = state.formulario
    val producto = state.producto

    // Aplica las restricciones del perfil una vez (el Vendedor solo registra salidas)
    LaunchedEffect(perfil) { viewModel.aplicarPerfil(perfil) }

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(titulo = "Registrar movimiento", onVolver = onVolver)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MensajeExito(mensaje = form.mensajeExito, onOcultar = viewModel::onMensajeMostrado)

            SelectorDesplegable(
                etiqueta = "Producto",
                opciones = state.productos,
                seleccionada = producto,
                onSeleccion = { viewModel.onProductoChanged(it.id) },
                texto = { it.nombre },
                error = form.errores.producto,
                icono = Icons.Filled.Inventory2,
                ayuda = producto?.let {
                    "${it.codigo} · Stock actual: ${it.cantidad} ${it.unidad.abreviatura}"
                }
            )

            SelectorOpciones(
                titulo = if (form.soloSalidas) "Tipo de movimiento (tu perfil solo registra salidas)"
                else "Tipo de movimiento",
                opciones = TipoMovimiento.entries,
                seleccionada = form.tipo,
                onSeleccion = viewModel::onTipoChanged,
                texto = { it.etiqueta },
                habilitado = !form.soloSalidas
            )

            CampoTexto(
                valor = form.cantidad,
                onChange = viewModel::onCantidadChanged,
                etiqueta = "Cantidad" + (producto?.let { " (${it.unidad.abreviatura})" } ?: ""),
                error = form.errores.cantidad,
                icono = Icons.Filled.Numbers,
                tipoTeclado = KeyboardType.Number
            )

            SelectorOpciones(
                titulo = if (form.tipo == TipoMovimiento.SALIDA) "Motivo de la salida" else "Motivo del ingreso",
                opciones = motivosPara(form.tipo),
                seleccionada = form.motivo,
                onSeleccion = viewModel::onMotivoChanged,
                texto = { it },
                error = form.errores.motivo
            )

            Text(
                text = "${System.currentTimeMillis().comoFecha()} · Perfil: ${perfil.nombre}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Vista previa del stock resultante; aparece al escribir una cantidad válida
            val resultante = state.stockResultante
            AnimatedVisibility(visible = producto != null && resultante != null) {
                val fondo by animateColorAsState(
                    if (state.quedaBajoMinimo) StockBajoFondo else DisponibleFondo, label = "fondoResultante"
                )
                val texto = if (state.quedaBajoMinimo) StockBajoTexto else DisponibleTexto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = fondo, contentColor = texto)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Stock resultante", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "${resultante ?: 0} ${producto?.unidad?.abreviatura.orEmpty()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.quedaBajoMinimo) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Quedará bajo el mínimo (${producto?.stockMinimo} ${producto?.unidad?.abreviatura})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.confirmar(perfil) },
                enabled = !form.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (form.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (form.tipo == TipoMovimiento.SALIDA) "Confirmar salida" else "Confirmar ingreso")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
