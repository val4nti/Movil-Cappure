package cl.duoc.cappucare.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Resultado de consulta que une un movimiento con su producto.
 * Se usa en el historial para mostrar el nombre del producto junto al movimiento.
 */
data class MovimientoConProducto(
    @Embedded val movimiento: MovimientoEntity,
    @Relation(
        parentColumn = "productoId",
        entityColumn = "id"
    )
    val producto: ProductoEntity
)
