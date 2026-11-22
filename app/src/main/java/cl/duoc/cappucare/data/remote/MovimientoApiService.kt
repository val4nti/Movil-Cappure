package cl.duoc.cappucare.data.remote

import cl.duoc.cappucare.data.remote.dto.MovimientoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Endpoints del microservicio de movimientos (puerto 8082).
 * Al registrar un movimiento, el propio microservicio actualiza el stock en productos-service.
 */
interface MovimientoApiService {

    @GET("api/movimientos")
    suspend fun listar(): Response<List<MovimientoDto>>

    @POST("api/movimientos")
    suspend fun registrar(@Body movimiento: MovimientoDto): Response<MovimientoDto>

    @DELETE("api/movimientos/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
