package cl.duoc.cappucare.ui.screens.detalle

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.data.local.entity.estadoStock
import cl.duoc.cappucare.data.local.entity.unidadesPorReponer
import cl.duoc.cappucare.model.EstadoStock
import cl.duoc.cappucare.ui.components.BarraSuperior
import cl.duoc.cappucare.ui.components.EstadoChip
import cl.duoc.cappucare.ui.components.ImagenProducto
import cl.duoc.cappucare.ui.theme.AgotadoTexto
import cl.duoc.cappucare.ui.theme.DisponibleTexto
import cl.duoc.cappucare.ui.theme.StockBajoTexto
import cl.duoc.cappucare.utils.comoFecha
import cl.duoc.cappucare.viewmodel.DetalleProductoViewModel

/**
 * Detalle de un producto: foto, cantidad, nivel mínimo y datos.
 *
 * Aquí se usan dos recursos nativos:
 * - Cámara: [ActivityResultContracts.TakePicture], pidiendo antes el permiso CAMERA.
 * - Galería: [ActivityResultContracts.PickVisualMedia] (selector de fotos de Android, no pide permiso).
 *
 * @param puedeEditarFoto true si el perfil puede cambiar la foto (Encargado de bodega).
 * @param mostrarMensaje Muestra un mensaje breve (Snackbar) en la parte inferior.
 */
@Composable
fun DetalleProductoScreen(
    onVolver: () -> Unit,
    onRegistrarMovimiento: (Long) -> Unit,
    onVerHistorial: (Long) -> Unit,
    mostrarMensaje: (String) -> Unit,
    puedeEditarFoto: Boolean,
    modifier: Modifier = Modifier,
    viewModel: DetalleProductoViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Uri donde la cámara escribirá la foto. Se guarda con rememberSaveable para no perderla
    // si Android recrea la pantalla mientras la cámara está abierta.
    var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

    // 1) Lanzador de la cámara: devuelve true si el usuario aceptó la foto
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = uriPendiente
        if (exito && uri != null) {
            viewModel.onFotoTomada(uri)
            mostrarMensaje("Foto guardada")
        }
        // Si el usuario canceló, no pasa nada (la app no se cae)
        uriPendiente = null
    }

    fun abrirCamara() {
        val uri = viewModel.crearUriParaFoto()
        uriPendiente = uri
        camaraLauncher.launch(uri)
    }

    // 2) Lanzador para pedir el permiso de cámara en tiempo de ejecución
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else mostrarMensaje("Sin permiso de cámara no se puede tomar la foto")
    }

    // 3) Lanzador de la galería: devuelve la Uri de la imagen elegida, o null si se canceló
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImagenDeGaleria(uri) { ok ->
                mostrarMensaje(if (ok) "Imagen guardada" else "No se pudo leer la imagen")
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(titulo = "Inventario", onVolver = onVolver)

        val producto = state.producto
        when {
            state.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            producto == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("El producto no existe")
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Encabezado con foto y nombre
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ImagenProducto(fotoUri = producto.fotoUri)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${producto.codigo} · ${producto.categoria}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botones de cámara y galería
                if (puedeEditarFoto) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (tienePermiso) abrirCamara()
                            else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Cámara")
                        }
                        OutlinedButton(onClick = {
                            galeriaLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Galería")
                        }
                        if (producto.fotoUri != null) {
                            TextButton(onClick = viewModel::quitarFoto) { Text("Quitar") }
                        }
                    }
                }

                // Tarjeta de stock con barra animada respecto del mínimo
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = producto.cantidad.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${producto.unidad.abreviatura} disponibles",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Spacer(Modifier.weight(1f))
                            EstadoChip(producto.estadoStock, Modifier.padding(bottom = 10.dp))
                        }

                        val progresoObjetivo = if (producto.stockMinimo == 0) 1f
                        else (producto.cantidad.toFloat() / producto.stockMinimo).coerceIn(0f, 1f)
                        val progreso by animateFloatAsState(progresoObjetivo, label = "progresoStock")
                        val colorBarra = when (producto.estadoStock) {
                            EstadoStock.DISPONIBLE -> DisponibleTexto
                            EstadoStock.STOCK_BAJO -> StockBajoTexto
                            EstadoStock.AGOTADO -> AgotadoTexto
                        }
                        LinearProgressIndicator(
                            progress = { progreso },
                            color = colorBarra,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Mínimo: ${producto.stockMinimo} ${producto.unidad.abreviatura}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (producto.unidadesPorReponer > 0) {
                            Text(
                                text = "Reponer al menos ${producto.unidadesPorReponer} ${producto.unidad.abreviatura}",
                                color = colorBarra,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Datos del producto
                Dato("Descripción", producto.descripcion.ifBlank { "Sin descripción" })
                HorizontalDivider()
                Dato("Unidad de medida", producto.unidad.nombre)
                HorizontalDivider()
                Dato("Ubicación", producto.ubicacion)
                HorizontalDivider()
                Dato(
                    "Último movimiento",
                    state.ultimoMovimiento?.let {
                        "${it.tipo.etiqueta} de ${it.cantidad} · ${it.fecha.comoFecha()}"
                    } ?: "Sin movimientos"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onRegistrarMovimiento(producto.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.SwapVert, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Registrar movimiento")
                    }
                    OutlinedButton(onClick = { onVerHistorial(producto.id) }) {
                        Icon(Icons.Filled.History, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Historial")
                    }
                }
                Spacer(Modifier.padding(8.dp))
            }
        }
    }
}

/** Un dato con su título arriba, en gris. */
@Composable
private fun Dato(titulo: String, valor: String) {
    Column {
        Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
