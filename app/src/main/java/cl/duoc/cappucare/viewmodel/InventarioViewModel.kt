package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.data.local.entity.estadoStock
import cl.duoc.cappucare.model.EstadoStock
import cl.duoc.cappucare.model.FiltroInventario
import cl.duoc.cappucare.model.InventarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de inventario.
 *
 * Combina tres fuentes de datos en un solo [InventarioUiState]:
 * 1. La lista de productos que viene de Room (a través del repositorio).
 * 2. El texto del buscador.
 * 3. El filtro seleccionado (Todos / Stock bajo).
 * Cada vez que cualquiera de las tres cambia, se calcula un estado nuevo.
 */
class InventarioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))

    // Estados privados que solo el ViewModel modifica
    private val busqueda = MutableStateFlow("")
    private val filtro = MutableStateFlow(FiltroInventario.TODOS)

    /** Estado público de solo lectura que observa la pantalla. */
    val uiState: StateFlow<InventarioUiState> = combine(
        repository.productos,
        busqueda,
        filtro
    ) { productos, textoBusqueda, filtroActual ->

        // Productos que necesitan reposición: agotados o bajo el mínimo
        val conStockBajo = productos.filter { it.estadoStock != EstadoStock.DISPONIBLE }

        val base = when (filtroActual) {
            FiltroInventario.TODOS -> productos
            FiltroInventario.STOCK_BAJO -> conStockBajo
        }

        // Se busca por nombre o por código, sin importar mayúsculas
        val texto = textoBusqueda.trim()
        val filtrados = if (texto.isEmpty()) base else base.filter {
            it.nombre.contains(texto, ignoreCase = true) || it.codigo.contains(texto)
        }

        InventarioUiState(
            productos = filtrados,
            busqueda = textoBusqueda,
            filtro = filtroActual,
            totalProductos = productos.size,
            totalStockBajo = conStockBajo.size,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventarioUiState()
    )

    init {
        // La primera vez que se abre la app se carga el catálogo de ejemplo
        viewModelScope.launch {
            repository.cargarDatosInicialesSiVacio()
        }
    }

    fun onBusquedaChanged(texto: String) {
        busqueda.value = texto
    }

    fun onFiltroChanged(nuevoFiltro: FiltroInventario) {
        filtro.value = nuevoFiltro
    }
}
