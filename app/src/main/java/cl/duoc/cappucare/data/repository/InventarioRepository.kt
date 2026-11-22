package cl.duoc.cappucare.data.repository

import androidx.room.withTransaction
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.local.entity.MovimientoConProducto
import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.data.remote.ErrorServidor
import cl.duoc.cappucare.data.remote.MovimientoApiService
import cl.duoc.cappucare.data.remote.ProductoApiService
import cl.duoc.cappucare.data.remote.RetrofitInstance
import cl.duoc.cappucare.data.remote.aDto
import cl.duoc.cappucare.data.remote.aEntity
import cl.duoc.cappucare.data.remote.dto.ErrorDto
import cl.duoc.cappucare.data.remote.dto.MovimientoDto
import cl.duoc.cappucare.model.TipoMovimiento
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.IOException

/**
 * Repositorio: única puerta de entrada a los datos de inventario.
 *
 * Ahora los datos "de verdad" están en los microservicios (MySQL), y Room funciona como
 * una copia local en el teléfono:
 * - Para LEER, las pantallas observan Room (rápido y funciona aunque se caiga la red).
 * - Para ESCRIBIR (crear producto, registrar movimiento), se llama al microservicio con Retrofit
 *   y después se sincroniza: se descarga todo de nuevo y se reemplaza la copia local.
 *
 * Los ViewModels no saben nada de esto: siguen llamando a los mismos métodos de siempre.
 *
 * Las APIs se reciben en el constructor (con valor por defecto) para poder reemplazarlas
 * por versiones falsas en los tests unitarios.
 */
class InventarioRepository(
    private val database: AppDatabase,
    private val productoApi: ProductoApiService = RetrofitInstance.productoApi,
    private val movimientoApi: MovimientoApiService = RetrofitInstance.movimientoApi
) {

    private val productoDao = database.productoDao
    private val movimientoDao = database.movimientoDao

    // ---------------- Lectura (desde Room) ----------------

    val productos: Flow<List<ProductoEntity>> = productoDao.obtenerTodos()

    val historial: Flow<List<MovimientoConProducto>> = movimientoDao.obtenerHistorial()

    fun historialDeProducto(productoId: Long): Flow<List<MovimientoConProducto>> =
        movimientoDao.obtenerHistorialDeProducto(productoId)

    fun observarProducto(id: Long): Flow<ProductoEntity?> = productoDao.observarPorId(id)

    fun ultimoMovimiento(productoId: Long): Flow<MovimientoEntity?> =
        movimientoDao.observarUltimo(productoId)

    suspend fun buscarPorCodigo(codigo: String): ProductoEntity? =
        productoDao.obtenerPorCodigo(codigo)

    /** La foto queda solo en el teléfono (el servidor no guarda imágenes). */
    suspend fun actualizarFoto(productoId: Long, fotoUri: String?) =
        productoDao.actualizarFoto(productoId, fotoUri)

    // ---------------- Sincronización (microservicios → Room) ----------------

    /**
     * Descarga productos (GET 8081) y movimientos (GET 8082) y reemplaza la copia local.
     * Va dentro de una transacción para que las pantallas nunca vean la base a medio actualizar.
     *
     * @throws ErrorServidor si algún microservicio no responde.
     */
    suspend fun sincronizar() {
        val productosRemotos = llamarConDatos { productoApi.listar() }
        val movimientosRemotos = llamarConDatos { movimientoApi.listar() }

        database.withTransaction {
            // Las fotos solo existen en el teléfono: se guardan antes de borrar para no perderlas
            val fotos = productoDao.obtenerTodosUnaVez().associate { it.id to it.fotoUri }

            movimientoDao.borrarTodos()
            productoDao.borrarTodos()

            productoDao.insertarTodos(productosRemotos.map { dto -> dto.aEntity(dto.id?.let { fotos[it] }) })

            // Solo movimientos de productos que existen (Room exige la clave foránea)
            val ids = productosRemotos.mapNotNull { it.id }.toSet()
            movimientoDao.insertarTodos(
                movimientosRemotos.filter { it.productoId in ids }.map { it.aEntity() }
            )
        }
    }

    // ---------------- Escritura (Room → microservicios) ----------------

    /**
     * Crea un producto en productos-service. Si trae cantidad inicial, se registra como un
     * ingreso "Stock inicial" en movimientos-service, para que el historial cuadre con el stock.
     *
     * @return el id que le asignó MySQL al producto.
     * @throws ErrorServidor por ejemplo con código 409 si el código ya existe.
     */
    suspend fun agregarProducto(producto: ProductoEntity, perfil: String): Long {
        // El producto parte en 0: la cantidad inicial entra como movimiento
        val creado = llamarConDatos { productoApi.crear(producto.copy(cantidad = 0).aDto()) }
        val id = creado.id ?: throw ErrorServidor("El servidor no devolvió el id del producto")

        if (producto.cantidad > 0) {
            llamarConDatos {
                movimientoApi.registrar(
                    MovimientoDto(
                        productoId = id,
                        tipo = TipoMovimiento.INGRESO.name,
                        cantidad = producto.cantidad,
                        motivo = "Stock inicial",
                        perfil = perfil
                    )
                )
            }
        }

        sincronizar()
        return id
    }

    /**
     * Registra un ingreso o salida en movimientos-service. El microservicio se encarga de
     * actualizar el stock en productos-service (y rechaza la salida si no alcanza).
     *
     * @return el producto con la cantidad ya actualizada.
     * @throws ErrorServidor por ejemplo "No hay stock suficiente para esta salida".
     */
    suspend fun registrarMovimiento(movimiento: MovimientoEntity): ProductoEntity {
        llamarConDatos { movimientoApi.registrar(movimiento.aDto()) }
        sincronizar()
        return productoDao.obtenerPorId(movimiento.productoId)
            ?: throw ErrorServidor("El producto no existe")
    }

    // ---------------- Ayudantes para llamar a Retrofit ----------------

    /**
     * Ejecuta una llamada de Retrofit y devuelve el cuerpo de la respuesta.
     * Convierte cualquier problema en un [ErrorServidor] con un mensaje entendible.
     */
    private suspend fun <T> llamarConDatos(llamada: suspend () -> Response<T>): T {
        val respuesta = try {
            llamada()
        } catch (e: IOException) {
            // No hubo respuesta: microservicio apagado, sin internet, IP equivocada...
            throw ErrorServidor("No se pudo conectar con el servidor. Revisa que los microservicios estén corriendo.")
        }

        if (!respuesta.isSuccessful) {
            throw ErrorServidor(mensajeDeError(respuesta), respuesta.code())
        }
        return respuesta.body() ?: throw ErrorServidor("El servidor respondió sin datos")
    }

    /** Lee el "message" del JSON de error de Spring Boot, o arma uno genérico. */
    private fun mensajeDeError(respuesta: Response<*>): String {
        val texto = respuesta.errorBody()?.string()
        val mensaje = try {
            Gson().fromJson(texto, ErrorDto::class.java)?.message
        } catch (e: Exception) {
            null
        }
        return if (mensaje.isNullOrBlank()) "Error del servidor (${respuesta.code()})" else mensaje
    }
}
