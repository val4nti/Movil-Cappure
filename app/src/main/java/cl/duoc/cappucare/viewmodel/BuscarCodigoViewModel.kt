package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.repository.InventarioRepository
import cl.duoc.cappucare.utils.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class BuscarCodigoUiState(
    val codigo: String = "",
    val error: String? = null,
    val noEncontrado: Boolean = false
)


class BuscarCodigoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(AppDatabase.obtener(application))

    private val _state = MutableStateFlow(BuscarCodigoUiState())
    val state: StateFlow<BuscarCodigoUiState> = _state.asStateFlow()


    fun onDigito(digito: Char) = _state.update {
        if (it.codigo.length >= Validaciones.LARGO_CODIGO) it
        else BuscarCodigoUiState(codigo = it.codigo + digito)
    }

    fun onBorrar() = _state.update { BuscarCodigoUiState(codigo = it.codigo.dropLast(1)) }


    fun onEjemplo() = _state.update { BuscarCodigoUiState(codigo = "7801234000112") }


    fun buscar(onEncontrado: (Long) -> Unit) {
        val codigo = _state.value.codigo
        val error = Validaciones.codigo(codigo)
        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            val producto = repository.buscarPorCodigo(codigo)
            if (producto != null) onEncontrado(producto.id)
            else _state.update { it.copy(noEncontrado = true) }
        }
    }
}
