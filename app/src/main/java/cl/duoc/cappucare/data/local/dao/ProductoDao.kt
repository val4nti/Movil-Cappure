package cl.duoc.cappucare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO con las operaciones sobre la tabla "productos".
 * Las consultas que devuelven [Flow] se actualizan solas cuando cambian los datos.
 */
@Dao
interface ProductoDao {

    /** Todos los productos activos, ordenados por nombre. */
    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    /** Un producto por su id, observado en tiempo real (para la pantalla de detalle). */
    @Query("SELECT * FROM productos WHERE id = :id")
    fun observarPorId(id: Long): Flow<ProductoEntity?>

    /** Un producto por su id, leído una sola vez. */
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ProductoEntity?

    /** Busca un producto por su código de barras. */
    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): ProductoEntity?

    /** Cantidad de productos guardados. Se usa para saber si hay que cargar datos de ejemplo. */
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    /** Inserta un producto y devuelve el id generado. */
    @Insert
    suspend fun insertar(producto: ProductoEntity): Long

    @Insert
    suspend fun insertarTodos(productos: List<ProductoEntity>): List<Long>

    @Update
    suspend fun actualizar(producto: ProductoEntity)

    /** Guarda la Uri de la foto de un producto. */
    @Query("UPDATE productos SET fotoUri = :fotoUri WHERE id = :id")
    suspend fun actualizarFoto(id: Long, fotoUri: String?)
}
