package cl.duoc.cappucare.data.remote.dto

/**
 * Forma del JSON de un movimiento en movimientos-service.
 * Ejemplo: {"productoId":1,"tipo":"SALIDA","cantidad":5,"motivo":"Venta","perfil":"Vendedor"}
 */
data class MovimientoDto(
    val id: Long? = null,           // null al crear
    val productoId: Long,
    val tipo: String,               // "INGRESO" o "SALIDA"
    val cantidad: Int,
    val motivo: String,
    val perfil: String,
    val fecha: String? = null       // la pone el servidor, ej. "2026-11-22T18:13:12.320495"
)
