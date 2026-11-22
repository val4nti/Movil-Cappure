package cl.duoc.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.remote.ErrorServidor
import cl.duoc.cappucare.data.repository.IndicadoresRepository
import cl.duoc.cappucare.model.IndicadoresUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la tarjeta "Dólar y UF hoy" (API externa).
 * Al crearse pide los indicadores; si falla, deja un mensaje de error y se puede reintentar.
 */
class IndicadoresViewModel(
    private val repository: IndicadoresRepository = IndicadoresRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndicadoresUiState())
    val uiState: StateFlow<IndicadoresUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = IndicadoresUiState(cargando = true)
            _uiState.value = try {
                IndicadoresUiState(cargando = false, indicadores = repository.obtenerIndicadores())
            } catch (e: ErrorServidor) {
                IndicadoresUiState(cargando = false, error = e.message)
            }
        }
    }
}
