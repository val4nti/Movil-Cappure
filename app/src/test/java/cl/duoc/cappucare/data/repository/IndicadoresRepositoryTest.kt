package cl.duoc.cappucare.data.repository

import cl.duoc.cappucare.data.remote.ErrorServidor
import cl.duoc.cappucare.data.remote.dto.IndicadoresDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/** Tests del repositorio de la API externa, usando una API falsa (sin internet). */
class IndicadoresRepositoryTest {

    private val api = FakeIndicadoresApi()
    private val repository = IndicadoresRepository(api)

    @Test
    fun `respuesta correcta entrega dolar, uf y fecha`() = runTest {
        val indicadores = repository.obtenerIndicadores()
        assertEquals(948.12, indicadores.dolar, 0.001)
        assertEquals(39876.54, indicadores.uf, 0.001)
        assertEquals("2026-11-22", indicadores.fecha)
    }

    @Test
    fun `sin conexion lanza ErrorServidor con mensaje claro`() = runTest {
        api.sinConexion = true
        val error = capturarError()
        assertTrue(error.message!!.contains("sin conexión"))
    }

    @Test
    fun `error HTTP lanza ErrorServidor con el codigo`() = runTest {
        api.codigoError = 500
        assertEquals(500, capturarError().codigo)
    }

    @Test
    fun `respuesta incompleta lanza ErrorServidor`() = runTest {
        api.respuesta = IndicadoresDto(uf = null, dolar = null)
        assertTrue(capturarError().message!!.contains("incompleta"))
    }

    private suspend fun capturarError(): ErrorServidor {
        try {
            repository.obtenerIndicadores()
        } catch (e: ErrorServidor) {
            return e
        }
        fail("Se esperaba un ErrorServidor")
        throw IllegalStateException()
    }
}
