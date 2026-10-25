package cl.duoc.cappucare.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.model.DetalleUiState
import cl.duoc.cappucare.navigation.Argumentos
import cl.duoc.cappucare.utils.GestorImagenes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel del detalle de un producto. También guarda la foto del producto
 * tomada con la cámara o elegida de la galería.
 *
 * El id del producto llega en la ruta de navegación y se lee desde [SavedStateHandle].
 */
class DetalleProductoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))

    val productoId: Long = savedStateHandle.get<Long>(Argumentos.PRODUCTO_ID) ?: -1L

    val uiState: StateFlow<DetalleUiState> = combine(
        repository.observarProducto(productoId),
        repository.ultimoMovimiento(productoId)
    ) { producto, ultimo ->
        DetalleUiState(cargando = false, producto = producto, ultimoMovimiento = ultimo)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetalleUiState()
    )

    /** Crea la Uri donde la cámara guardará la foto. */
    fun crearUriParaFoto(): Uri = GestorImagenes.crearUriParaFoto(getApplication())

    /** La cámara terminó bien: se guarda la Uri de la foto en el producto. */
    fun onFotoTomada(uri: Uri) {
        viewModelScope.launch { repository.actualizarFoto(productoId, uri.toString()) }
    }

    /**
     * Se eligió una imagen en la galería: se copia a la carpeta de la app
     * (en un hilo de entrada/salida para no congelar la pantalla) y se guarda en el producto.
     *
     * @return se informa el resultado con [onResultado] (true si se guardó).
     */
    fun onImagenDeGaleria(uri: Uri, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val copia = withContext(Dispatchers.IO) {
                runCatching { GestorImagenes.copiarDesdeGaleria(getApplication(), uri) }.getOrNull()
            }
            if (copia != null) repository.actualizarFoto(productoId, copia.toString())
            onResultado(copia != null)
        }
    }

    fun quitarFoto() {
        viewModelScope.launch { repository.actualizarFoto(productoId, null) }
    }
}
