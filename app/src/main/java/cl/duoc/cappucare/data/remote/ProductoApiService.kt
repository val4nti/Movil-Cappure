package cl.duoc.cappucare.data.remote

import cl.duoc.cappucare.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Endpoints del microservicio de productos (puerto 8081).
 *
 * Retrofit genera el código de red a partir de esta interfaz:
 * cada método con @GET/@POST/@PUT/@DELETE se convierte en una petición HTTP.
 * - `suspend` hace la llamada con corrutinas, sin bloquear la pantalla.
 * - `Response<...>` permite revisar el código HTTP (200, 404, 409...) con `isSuccessful`.
 */
interface ProductoApiService {

    @GET("api/productos")
    suspend fun listar(): Response<List<ProductoDto>>

    @GET("api/productos/{id}")
    suspend fun buscarPorId(@Path("id") id: Long): Response<ProductoDto>

    @POST("api/productos")
    suspend fun crear(@Body producto: ProductoDto): Response<ProductoDto>

    @PUT("api/productos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body producto: ProductoDto): Response<ProductoDto>

    @DELETE("api/productos/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
