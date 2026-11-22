package cl.duoc.cappucare.data.remote.dto

/**
 * Forma del JSON de un producto tal como lo envía y recibe productos-service.
 * Gson convierte automáticamente el JSON en esta clase (y al revés), por eso
 * los nombres de las propiedades son iguales a los del JSON.
 *
 * DTO = "Data Transfer Object": objeto que solo sirve para transportar datos por la red.
 * Es distinto de ProductoEntity (Room), que es la tabla local del teléfono.
 */
data class ProductoDto(
    val id: Long? = null,           // null al crear: el id lo pone la base de datos MySQL
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val categoria: String,
    val cantidad: Int,
    val unidad: String,             // "UNIDAD", "CAJA" o "PAQUETE"
    val stockMinimo: Int,
    val ubicacion: String?,
    val activo: Boolean? = true
)
