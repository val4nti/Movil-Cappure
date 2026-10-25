package cl.duoc.cappucare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.duoc.cappucare.data.local.entity.MovimientoConProducto
import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO con las operaciones sobre la tabla "movimientos" (el historial de existencias).
 */
@Dao
interface MovimientoDao {

    /** Historial completo, del más reciente al más antiguo, con el producto de cada movimiento. */
    @Transaction
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun obtenerHistorial(): Flow<List<MovimientoConProducto>>

    /** Historial de un solo producto, del más reciente al más antiguo. */
    @Transaction
    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fecha DESC")
    fun obtenerHistorialDeProducto(productoId: Long): Flow<List<MovimientoConProducto>>

    /** Último movimiento de un producto (o null si no tiene). */
    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fecha DESC LIMIT 1")
    fun observarUltimo(productoId: Long): Flow<MovimientoEntity?>

    @Insert
    suspend fun insertarTodos(movimientos: List<MovimientoEntity>)

    @Insert
    suspend fun insertar(movimiento: MovimientoEntity): Long
}
