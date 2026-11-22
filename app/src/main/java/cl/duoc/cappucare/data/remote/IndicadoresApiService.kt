package cl.duoc.cappucare.data.remote

import cl.duoc.cappucare.data.remote.dto.IndicadoresDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * API EXTERNA: mindicador.cl entrega indicadores económicos de Chile (dólar, UF, euro...).
 *
 * Diferencia con los microservicios:
 * - Los microservicios (8081 y 8082) los programamos nosotras, tienen nuestra base de datos
 *   y podemos crear, editar y borrar (CRUD).
 * - Esta API es de un tercero: solo la consultamos (GET), no controlamos sus datos ni su código.
 */
interface IndicadoresApiService {

    /** GET https://mindicador.cl/api → todos los indicadores del día. */
    @GET("api")
    suspend fun obtenerIndicadores(): Response<IndicadoresDto>
}
