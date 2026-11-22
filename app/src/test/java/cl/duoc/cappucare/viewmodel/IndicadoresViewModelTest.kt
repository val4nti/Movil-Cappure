package cl.duoc.cappucare.viewmodel

import cl.duoc.cappucare.MainDispatcherRule
import cl.duoc.cappucare.data.repository.FakeIndicadoresApi
import cl.duoc.cappucare.data.repository.IndicadoresRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests del ViewModel de la API externa.
 * Se revisa el estado que verá la pantalla: cargando → datos, o cargando → error.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class IndicadoresViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeIndicadoresApi()

    @Test
    fun `al crearse parte cargando y luego muestra los indicadores`() = runTest {
        val viewModel = IndicadoresViewModel(IndicadoresRepository(api))
        assertTrue(viewModel.uiState.value.cargando)

        advanceUntilIdle()   // deja terminar la corrutina que pide los datos

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals(948.12, estado.indicadores!!.dolar, 0.001)
        assertNull(estado.error)
    }

    @Test
    fun `si falla la API muestra el error`() = runTest {
        api.sinConexion = true
        val viewModel = IndicadoresViewModel(IndicadoresRepository(api))
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertNull(estado.indicadores)
        assertNotNull(estado.error)
    }

    @Test
    fun `reintentar vuelve a pedir y se recupera`() = runTest {
        api.sinConexion = true
        val viewModel = IndicadoresViewModel(IndicadoresRepository(api))
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        api.sinConexion = false
        viewModel.cargar()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.indicadores)
        assertNull(viewModel.uiState.value.error)
        assertEquals(2, api.llamadas)
    }
}
