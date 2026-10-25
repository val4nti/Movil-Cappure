package cl.duoc.cappucare.ui.screens.alertas

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.data.local.entity.estadoStock
import cl.duoc.cappucare.data.local.entity.unidadesPorReponer
import cl.duoc.cappucare.ui.components.BarraSuperior
import cl.duoc.cappucare.ui.components.EstadoChip
import cl.duoc.cappucare.utils.Notificaciones
import cl.duoc.cappucare.viewmodel.AlertasViewModel

/**
 * Lista de productos que necesitan reposición y el interruptor para recibir notificaciones.
 *
 * Al activar el interruptor en Android 13 o superior se pide el permiso POST_NOTIFICATIONS.
 *
 * @param puedeIngresar true si el perfil puede registrar ingresos (Encargado de bodega).
 */
@Composable
fun AlertasScreen(
    onVolver: () -> Unit,
    onRegistrarIngreso: (Long) -> Unit,
    mostrarMensaje: (String) -> Unit,
    puedeIngresar: Boolean,
    modifier: Modifier = Modifier,
    viewModel: AlertasViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val avisar by viewModel.avisarMinimo.collectAsState()
    val context = LocalContext.current

    // Pide el permiso de notificaciones y guarda la preferencia según la respuesta
    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.cambiarAvisarMinimo(concedido)
        if (!concedido) mostrarMensaje("Sin permiso no se pueden mostrar notificaciones")
    }

    fun onCambiarAviso(activar: Boolean) {
        if (activar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !Notificaciones.tienePermiso(context)
        ) {
            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.cambiarAvisarMinimo(activar)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(titulo = "Alertas de stock", onVolver = onVolver)

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = if (productos.size == 1) "1 producto necesita reposición"
                    else "${productos.size} productos necesitan reposición",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Avisar al llegar al mínimo", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (avisar && !Notificaciones.tienePermiso(context))
                                    "Falta el permiso de notificaciones"
                                else "Notificación en el dispositivo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = avisar, onCheckedChange = { onCambiarAviso(it) })
                    }
                }
            }
            items(productos, key = { it.id }) { producto ->
                TarjetaAlerta(
                    producto = producto,
                    puedeIngresar = puedeIngresar,
                    onRegistrarIngreso = { onRegistrarIngreso(producto.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun TarjetaAlerta(
    producto: ProductoEntity,
    puedeIngresar: Boolean,
    onRegistrarIngreso: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(producto.nombre, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                EstadoChip(producto.estadoStock)
            }
            Text(
                "Stock: ${producto.cantidad} ${producto.unidad.abreviatura} · mín. ${producto.stockMinimo}",
                style = MaterialTheme.typography.bodySmall
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Reponer ${producto.unidadesPorReponer} ${producto.unidad.abreviatura}",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (puedeIngresar) {
                    TextButton(onClick = onRegistrarIngreso) { Text("Registrar ingreso") }
                }
            }
        }
    }
}
