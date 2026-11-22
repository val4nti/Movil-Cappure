package cl.duoc.cappucare.data.repository

import cl.duoc.cappucare.data.remote.IndicadoresApiService
import cl.duoc.cappucare.data.remote.dto.IndicadorDto
import cl.duoc.cappucare.data.remote.dto.IndicadoresDto
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * API falsa de mindicador.cl para los tests: no usa internet.
 * Se configura qué debe responder: datos correctos, un error HTTP o "sin conexión".
 */
class FakeIndicadoresApi : IndicadoresApiService {

    var respuesta: IndicadoresDto? = IndicadoresDto(
        uf = IndicadorDto("uf", "Unidad de fomento (UF)", "Pesos", "2026-11-22T03:00:00.000Z", 39876.54),
        dolar = IndicadorDto("dolar", "Dólar observado", "Pesos", "2026-11-22T03:00:00.000Z", 948.12)
    )
    var codigoError: Int? = null
    var sinConexion = false
    var llamadas = 0

    override suspend fun obtenerIndicadores(): Response<IndicadoresDto> {
        llamadas++
        if (sinConexion) throw IOException("Sin internet")
        codigoError?.let { return Response.error(it, "".toResponseBody(null)) }
        return Response.success(respuesta)
    }
}
