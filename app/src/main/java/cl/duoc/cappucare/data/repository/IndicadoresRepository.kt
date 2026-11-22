package cl.duoc.cappucare.data.repository

import cl.duoc.cappucare.data.remote.ErrorServidor
import cl.duoc.cappucare.data.remote.IndicadoresApiService
import cl.duoc.cappucare.data.remote.RetrofitInstance
import cl.duoc.cappucare.model.Indicadores
import java.io.IOException

/**
 * Repositorio de la API externa (mindicador.cl).
 * Pide los indicadores con Retrofit y los convierte en un modelo simple para la pantalla.
 *
 * La API se recibe en el constructor para poder usar una falsa en los tests.
 */
class IndicadoresRepository(
    private val api: IndicadoresApiService = RetrofitInstance.indicadoresApi
) {

    /** @throws ErrorServidor si no hay conexión o la respuesta viene incompleta. */
    suspend fun obtenerIndicadores(): Indicadores {
        val respuesta = try {
            api.obtenerIndicadores()
        } catch (e: IOException) {
            throw ErrorServidor("No se pudo consultar el dólar y la UF (sin conexión)")
        }

        if (!respuesta.isSuccessful) {
            throw ErrorServidor("mindicador.cl respondió con error ${respuesta.code()}", respuesta.code())
        }

        val cuerpo = respuesta.body()
        val dolar = cuerpo?.dolar?.valor
        val uf = cuerpo?.uf?.valor
        if (dolar == null || uf == null) {
            throw ErrorServidor("La respuesta de mindicador.cl vino incompleta")
        }

        return Indicadores(
            dolar = dolar,
            uf = uf,
            // "2026-11-22T03:00:00.000Z" → "2026-11-22"
            fecha = cuerpo?.dolar?.fecha?.take(10).orEmpty()
        )
    }
}
