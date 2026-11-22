package cl.duoc.cappucare.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Configuración única de Retrofit para los dos microservicios y la API externa.
 *
 * ¿Por qué 10.0.2.2? Dentro del emulador, "localhost" es el propio emulador.
 * 10.0.2.2 es la dirección especial que apunta al PC donde corren los microservicios.
 * Si usas un celular físico, cambia 10.0.2.2 por la IP de tu PC en la red Wi-Fi
 * (en Windows: comando `ipconfig`, "Dirección IPv4", por ejemplo 192.168.1.15).
 */
object RetrofitInstance {

    // La URL base debe terminar en "/" para que Retrofit una bien las rutas
    private const val URL_PRODUCTOS = "http://10.0.2.2:8081/"
    private const val URL_MOVIMIENTOS = "http://10.0.2.2:8082/"

    // API externa (de terceros, con HTTPS)
    private const val URL_MINDICADOR = "https://mindicador.cl/"

    /** `by lazy`: el objeto se crea la primera vez que se usa, y después se reutiliza. */
    val productoApi: ProductoApiService by lazy { crear(URL_PRODUCTOS) }

    val movimientoApi: MovimientoApiService by lazy { crear(URL_MOVIMIENTOS) }

    val indicadoresApi: IndicadoresApiService by lazy { crear(URL_MINDICADOR) }

    private inline fun <reified T> crear(baseUrl: String): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            // Gson convierte el JSON de la respuesta en los DTO (y los DTO en JSON al enviar)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(T::class.java)
}
