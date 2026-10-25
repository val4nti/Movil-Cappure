package cl.duoc.cappucare.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.cappucare.model.EstadoStock
import cl.duoc.cappucare.model.UnidadMedida

/**
 * Entidad de Room que representa la tabla "productos".
 * Cada fila es un producto o insumo médico que Cappucare vende.
 *
 * El código es único: no pueden existir dos productos con el mismo código de barras.
 */
@Entity(
    tableName = "productos",
    indices = [Index(value = ["codigo"], unique = true)]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,                 // Identificador interno autoincremental
    val codigo: String,               // Código de barras (13 dígitos) o identificador del producto
    val nombre: String,
    val descripcion: String,
    val categoria: String,            // Una de las opciones de CATEGORIAS
    val cantidad: Int,                // Unidades disponibles actualmente
    val unidad: UnidadMedida,         // Room guarda el enum como texto (su nombre)
    val stockMinimo: Int,             // Bajo este nivel el producto necesita reposición
    val ubicacion: String,            // Ej. "Bodega A · Estante 3"
    val activo: Boolean = true,       // Estado del producto: false si ya no se comercializa
    val fotoUri: String? = null       // Foto tomada con la cámara o elegida de la galería (opcional)
)

/**
 * Estado calculado a partir de la cantidad y el mínimo.
 * Es una propiedad de extensión (fuera de la clase) para que Room no intente guardarla como columna.
 */
val ProductoEntity.estadoStock: EstadoStock
    get() = EstadoStock.calcular(cantidad, stockMinimo)

/** Unidades que faltan para volver a llegar al nivel mínimo (0 si no falta nada). */
val ProductoEntity.unidadesPorReponer: Int
    get() = (stockMinimo - cantidad).coerceAtLeast(0)
