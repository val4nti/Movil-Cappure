package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.model.FiltroHistorial
import cl.duoc.cappucare.model.HistorialUiState
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.navigation.Argumentos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel del historial de movimientos. Puede mostrar todos los movimientos
 * o solo los de un producto (si llega un productoId en la ruta).
 */
class HistorialViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))
    private val productoId = savedStateHandle.get<Long>(Argumentos.PRODUCTO_ID) ?: -1L
    private val filtro = MutableStateFlow(FiltroHistorial.TODOS)

    private val origen = if (productoId > 0) {
        repository.historialDeProducto(productoId)
    } else {
        repository.historial
    }

    val uiState: StateFlow<HistorialUiState> = combine(origen, filtro) { movimientos, filtroActual ->
        val filtrados = when (filtroActual) {
            FiltroHistorial.TODOS -> movimientos
            FiltroHistorial.INGRESOS -> movimientos.filter { it.movimiento.tipo == TipoMovimiento.INGRESO }
            FiltroHistorial.SALIDAS -> movimientos.filter { it.movimiento.tipo == TipoMovimiento.SALIDA }
        }
        HistorialUiState(
            movimientos = filtrados,
            filtro = filtroActual,
            nombreProducto = if (productoId > 0) movimientos.firstOrNull()?.producto?.nombre else null,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistorialUiState()
    )

    val esDeUnProducto: Boolean = productoId > 0

    fun onFiltroChanged(nuevo: FiltroHistorial) {
        filtro.value = nuevo
    }
}
