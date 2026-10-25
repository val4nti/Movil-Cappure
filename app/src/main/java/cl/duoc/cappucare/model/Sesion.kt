package cl.duoc.cappucare.model

/**
 * Perfiles de usuario del caso.
 * - ENCARGADO: gestiona productos y registra ingresos y salidas.
 * - VENDEDOR: consulta stock y solo registra salidas (ventas).
 */
enum class Perfil(val nombre: String, val descripcion: String) {
    ENCARGADO("Encargado de bodega", "Gestiona productos y movimientos"),
    VENDEDOR("Vendedor", "Consulta stock y registra ventas")
}

/** Usuario con la sesión iniciada. */
data class Sesion(
    val usuario: String,
    val perfil: Perfil
)
