package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.data.local.entity.estadoStock
import cl.duoc.cappucare.data.preferences.PreferenciasRepository
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.model.EstadoStock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de alertas: lista los productos que necesitan reposición
 * y guarda si el usuario quiere recibir notificaciones.
 */
class AlertasViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))
    private val preferencias = PreferenciasRepository(application)

    /** Productos agotados primero, luego los de stock bajo, cada grupo por nombre. */
    val productos: StateFlow<List<ProductoEntity>> = repository.productos
        .map { lista ->
            lista.filter { it.estadoStock != EstadoStock.DISPONIBLE }
                .sortedWith(compareBy({ it.estadoStock != EstadoStock.AGOTADO }, { it.nombre }))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val avisarMinimo: StateFlow<Boolean> = preferencias.avisarMinimo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun cambiarAvisarMinimo(activo: Boolean) {
        viewModelScope.launch { preferencias.cambiarAvisarMinimo(activo) }
    }
}
