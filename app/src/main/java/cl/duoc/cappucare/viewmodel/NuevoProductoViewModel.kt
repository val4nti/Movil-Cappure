package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.model.NuevoProductoUiState
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.UnidadMedida
import cl.duoc.cappucare.navigation.Argumentos
import cl.duoc.cappucare.utils.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del formulario "Nuevo producto".
 * Valida cada campo con [Validaciones] y revisa que el código no esté repetido.
 */
class NuevoProductoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))

    // Si se llega desde "Buscar por código", el código viene listo en la ruta
    private val _state = MutableStateFlow(
        NuevoProductoUiState(codigo = savedStateHandle.get<String>(Argumentos.CODIGO).orEmpty())
    )
    val state: StateFlow<NuevoProductoUiState> = _state.asStateFlow()

    // Cada cambio actualiza el campo y borra su error, para que el mensaje desaparezca al corregir
    fun onCodigoChanged(v: String) = _state.update {
        it.copy(codigo = v.filter { c -> c.isDigit() }.take(Validaciones.LARGO_CODIGO),
            errores = it.errores.copy(codigo = null))
    }
    fun onNombreChanged(v: String) = _state.update { it.copy(nombre = v, errores = it.errores.copy(nombre = null)) }
    fun onDescripcionChanged(v: String) = _state.update { it.copy(descripcion = v, errores = it.errores.copy(descripcion = null)) }
    fun onCategoriaChanged(v: String) = _state.update { it.copy(categoria = v, errores = it.errores.copy(categoria = null)) }
    fun onUnidadChanged(v: UnidadMedida) = _state.update { it.copy(unidad = v, errores = it.errores.copy(unidad = null)) }
    fun onCantidadChanged(v: String) = _state.update { it.copy(cantidad = v.filter { c -> c.isDigit() }.take(6), errores = it.errores.copy(cantidad = null)) }
    fun onMinimoChanged(v: String) = _state.update { it.copy(minimo = v.filter { c -> c.isDigit() }.take(6), errores = it.errores.copy(minimo = null)) }
    fun onUbicacionChanged(v: String) = _state.update { it.copy(ubicacion = v, errores = it.errores.copy(ubicacion = null)) }

    /** Aplica todas las reglas de validación. Devuelve true si el formulario es válido. */
    private fun validar(): Boolean {
        val s = _state.value
        val errores = s.errores.copy(
            codigo = Validaciones.codigo(s.codigo),
            nombre = Validaciones.nombre(s.nombre),
            descripcion = Validaciones.descripcion(s.descripcion),
            categoria = Validaciones.obligatorio(s.categoria, "una categoría"),
            unidad = if (s.unidad == null) "Selecciona una unidad" else null,
            cantidad = Validaciones.entero(s.cantidad, 0, "La cantidad"),
            minimo = Validaciones.entero(s.minimo, 1, "El mínimo"),
            ubicacion = Validaciones.ubicacion(s.ubicacion)
        )
        _state.update { it.copy(errores = errores) }
        return !errores.hayErrores
    }

    /**
     * Valida y guarda el producto. Si el código ya existe se muestra el error en ese campo.
     *
     * @param onGuardado se llama con el id del producto creado, para navegar a su detalle.
     */
    fun guardar(perfil: Perfil, onGuardado: (Long) -> Unit) {
        if (!validar() || _state.value.guardando) return

        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(guardando = true) }

            if (repository.buscarPorCodigo(s.codigo) != null) {
                _state.update {
                    it.copy(guardando = false,
                        errores = it.errores.copy(codigo = "Ya existe un producto con este código"))
                }
                return@launch
            }

            val id = repository.agregarProducto(
                producto = ProductoEntity(
                    codigo = s.codigo,
                    nombre = s.nombre.trim(),
                    descripcion = s.descripcion.trim(),
                    categoria = s.categoria!!,
                    cantidad = s.cantidad.toInt(),
                    unidad = s.unidad!!,
                    stockMinimo = s.minimo.toInt(),
                    ubicacion = s.ubicacion.trim()
                ),
                perfil = perfil.nombre
            )
            _state.update { it.copy(guardando = false) }
            onGuardado(id)
        }
    }
}
