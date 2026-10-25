package cl.duoc.cappucare.data.repository

import androidx.room.withTransaction
import cl.duoc.cappucare.data.local.AppDatabase
import cl.duoc.cappucare.data.local.DatosIniciales
import cl.duoc.cappucare.data.local.entity.MovimientoConProducto
import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: única puerta de entrada a los datos de inventario.
 *
 * Los ViewModels no hablan directamente con Room, sino con este repositorio.
 * Así, si más adelante los datos vienen de internet (sincronización), solo cambia esta clase
 * y el resto de la app sigue igual.
 */
class InventarioRepository(private val database: AppDatabase) {

    private val productoDao = database.productoDao
    private val movimientoDao = database.movimientoDao

    val productos: Flow<List<ProductoEntity>> = productoDao.obtenerTodos()

    val historial: Flow<List<MovimientoConProducto>> = movimientoDao.obtenerHistorial()

    fun historialDeProducto(productoId: Long): Flow<List<MovimientoConProducto>> =
        movimientoDao.obtenerHistorialDeProducto(productoId)

    fun observarProducto(id: Long): Flow<ProductoEntity?> = productoDao.observarPorId(id)

    fun ultimoMovimiento(productoId: Long): Flow<MovimientoEntity?> =
        movimientoDao.observarUltimo(productoId)

    suspend fun buscarPorCodigo(codigo: String): ProductoEntity? =
        productoDao.obtenerPorCodigo(codigo)

    suspend fun actualizarFoto(productoId: Long, fotoUri: String?) =
        productoDao.actualizarFoto(productoId, fotoUri)

    /**
     * Guarda un producto nuevo. Si trae cantidad inicial, también registra un ingreso
     * "Stock inicial" para que el historial cuadre con la cantidad desde el primer día.
     *
     * @return el id del producto creado.
     */
    suspend fun agregarProducto(producto: ProductoEntity, perfil: String): Long {
        return database.withTransaction {
            val id = productoDao.insertar(producto)
            if (producto.cantidad > 0) {
                movimientoDao.insertar(
                    MovimientoEntity(
                        productoId = id,
                        tipo = TipoMovimiento.INGRESO,
                        cantidad = producto.cantidad,
                        motivo = "Stock inicial",
                        perfil = perfil
                    )
                )
            }
            id
        }
    }

    /**
     * Registra un movimiento y actualiza la cantidad del producto.
     *
     * Ambas operaciones van dentro de una transacción: o se guardan las dos o ninguna.
     * Así el stock nunca queda distinto de lo que dice el historial.
     *
     * @return el producto con la cantidad ya actualizada.
     * @throws IllegalArgumentException si el producto no existe o si una salida deja el stock negativo.
     */
    suspend fun registrarMovimiento(movimiento: MovimientoEntity): ProductoEntity {
        return database.withTransaction {
            val producto = productoDao.obtenerPorId(movimiento.productoId)
                ?: throw IllegalArgumentException("El producto no existe")

            val nuevaCantidad = when (movimiento.tipo) {
                TipoMovimiento.INGRESO -> producto.cantidad + movimiento.cantidad
                TipoMovimiento.SALIDA -> producto.cantidad - movimiento.cantidad
            }
            require(nuevaCantidad >= 0) { "No hay stock suficiente para esta salida" }

            val actualizado = producto.copy(cantidad = nuevaCantidad)
            movimientoDao.insertar(movimiento)
            productoDao.actualizar(actualizado)
            actualizado
        }
    }

    /** Carga el catálogo y el historial de ejemplo solo si la base de datos está vacía (primer uso). */
    suspend fun cargarDatosInicialesSiVacio() {
        database.withTransaction {
            if (productoDao.contar() == 0) {
                val ids = productoDao.insertarTodos(DatosIniciales.productos)
                movimientoDao.insertarTodos(DatosIniciales.movimientos(ids))
            }
        }
    }
}
