package cl.duoc.cappucare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.cappucare.model.TipoMovimiento

/**
 * Entidad de Room que representa la tabla "movimientos".
 * Cada fila es un ingreso o una salida de unidades de un producto, y forma el historial.
 *
 * La clave foránea asegura que todo movimiento pertenezca a un producto existente.
 */
@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE   // Si se borra el producto, se borra su historial
        )
    ],
    indices = [Index("productoId")]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: Long,              // Producto al que pertenece el movimiento
    val tipo: TipoMovimiento,          // INGRESO o SALIDA
    val cantidad: Int,                 // Siempre positiva; el tipo indica si suma o resta
    val motivo: String,                // Ej. "Recepción", "Venta", "Merma", "Ajuste"
    val perfil: String,                // Perfil responsable del registro
    val fecha: Long = System.currentTimeMillis()
)
