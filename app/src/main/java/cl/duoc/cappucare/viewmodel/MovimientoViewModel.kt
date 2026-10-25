package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.preferences.PreferenciasRepository
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.model.FormularioMovimiento
import cl.duoc.cappucare.model.MovimientoUiState
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.model.motivosPara
import cl.duoc.cappucare.navigation.Argumentos
import cl.duoc.cappucare.utils.Notificaciones
import cl.duoc.cappucare.utils.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del formulario "Registrar movimiento" (ingresos y salidas de stock).
 *
 * Toda la validación ocurre aquí y no en la pantalla:
 * la pantalla solo muestra el estado y avisa cada cambio.
 */
class MovimientoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))
    private val preferencias = PreferenciasRepository(application)

    // Valores iniciales que pueden venir en la ruta (por ejemplo desde Detalle o Alertas)
    private val tipoInicial = savedStateHandle.get<String>(Argumentos.TIPO)
        ?.let { nombre -> TipoMovimiento.entries.firstOrNull { it.name == nombre } }
        ?: TipoMovimiento.SALIDA

    private val formulario = MutableStateFlow(
        FormularioMovimiento(
            productoId = savedStateHandle.get<Long>(Argumentos.PRODUCTO_ID) ?: -1L,
            tipo = tipoInicial,
            motivo = motivosPara(tipoInicial).first()
        )
    )

    val uiState: StateFlow<MovimientoUiState> = combine(
        repository.productos,
        formulario
    ) { productos, form ->
        MovimientoUiState(
            productos = productos,
            producto = productos.firstOrNull { it.id == form.productoId },
            formulario = form
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MovimientoUiState(formulario = formulario.value)
    )

    /** El Vendedor solo puede registrar salidas: se fuerza el tipo y se bloquea el selector. */
    fun aplicarPerfil(perfil: Perfil) {
        if (perfil == Perfil.VENDEDOR) {
            formulario.update {
                it.copy(
                    soloSalidas = true,
                    tipo = TipoMovimiento.SALIDA,
                    motivo = motivosPara(TipoMovimiento.SALIDA).first()
                )
            }
        }
    }

    fun onProductoChanged(productoId: Long) = formulario.update {
        it.copy(productoId = productoId, errores = it.errores.copy(producto = null, cantidad = null))
    }

    fun onTipoChanged(tipo: TipoMovimiento) = formulario.update {
        if (it.soloSalidas) it
        else it.copy(
            tipo = tipo,
            motivo = motivosPara(tipo).first(),       // Cada tipo tiene sus propios motivos
            errores = it.errores.copy(cantidad = null, motivo = null)
        )
    }

    /** Solo se aceptan dígitos, así el usuario no puede escribir letras ni signos. */
    fun onCantidadChanged(valor: String) = formulario.update {
        it.copy(
            cantidad = valor.filter { c -> c.isDigit() }.take(6),
            errores = it.errores.copy(cantidad = null)
        )
    }

    fun onMotivoChanged(motivo: String) = formulario.update {
        it.copy(motivo = motivo, errores = it.errores.copy(motivo = null))
    }

    fun onMensajeMostrado() = formulario.update { it.copy(mensajeExito = null) }

    /** Valida todos los campos. Devuelve true si no hay errores. */
    private fun validar(): Boolean {
        val estado = uiState.value
        val form = estado.formulario
        val errores = form.errores.copy(
            producto = if (estado.producto == null) "Selecciona un producto" else null,
            cantidad = Validaciones.cantidadMovimiento(
                valor = form.cantidad,
                esSalida = form.tipo == TipoMovimiento.SALIDA,
                stockActual = estado.producto?.cantidad ?: 0
            ),
            motivo = Validaciones.obligatorio(form.motivo, "un motivo")
        )
        formulario.update { it.copy(errores = errores) }
        return errores.producto == null && errores.cantidad == null && errores.motivo == null
    }

    /**
     * Valida y guarda el movimiento. Si el producto queda bajo el mínimo y las alertas
     * están activadas, muestra una notificación.
     */
    fun confirmar(perfil: Perfil) {
        if (!validar() || formulario.value.guardando) return
        val estado = uiState.value
        val producto = estado.producto ?: return
        val form = estado.formulario

        viewModelScope.launch {
            formulario.update { it.copy(guardando = true) }
            try {
                val actualizado = repository.registrarMovimiento(
                    MovimientoEntity(
                        productoId = producto.id,
                        tipo = form.tipo,
                        cantidad = form.cantidad.toInt(),
                        motivo = form.motivo.orEmpty(),
                        perfil = perfil.nombre
                    )
                )

                if (actualizado.cantidad < actualizado.stockMinimo && preferencias.avisarMinimo.first()) {
                    Notificaciones.avisarStockBajo(getApplication(), actualizado)
                }

                formulario.update {
                    it.copy(
                        cantidad = "",
                        guardando = false,
                        mensajeExito = (if (form.tipo == TipoMovimiento.INGRESO) "Ingreso registrado" else "Salida registrada") +
                            ". Stock actual: " +
                            "${actualizado.cantidad} ${actualizado.unidad.abreviatura}"
                    )
                }
            } catch (e: IllegalArgumentException) {
                // Por ejemplo, si otra persona vendió unidades justo antes y ya no alcanza
                formulario.update {
                    it.copy(guardando = false, errores = it.errores.copy(cantidad = e.message))
                }
            }
        }
    }
}
