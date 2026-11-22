package cl.duoc.cappucare.data.remote.dto

/**
 * Cuerpo de error que responde Spring Boot, por ejemplo:
 * {"status":400,"error":"Bad Request","message":"No hay stock suficiente para esta salida"}
 * Solo nos interesa el mensaje, para mostrarlo en pantalla.
 */
data class ErrorDto(
    val status: Int? = null,
    val message: String? = null
)
