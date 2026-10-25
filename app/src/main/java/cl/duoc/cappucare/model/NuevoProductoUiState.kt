package cl.duoc.cappucare.model

/** Mensajes de error del formulario de nuevo producto (null = sin error). */
data class NuevoProductoErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null,
    val unidad: String? = null,
    val cantidad: String? = null,
    val minimo: String? = null,
    val ubicacion: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(codigo, nombre, descripcion, categoria, unidad, cantidad, minimo, ubicacion)
            .any { it != null }
}

/** Estado del formulario de nuevo producto. Los números se guardan como texto mientras se escriben. */
data class NuevoProductoUiState(
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String? = null,
    val unidad: UnidadMedida? = null,
    val cantidad: String = "",
    val minimo: String = "",
    val ubicacion: String = "",
    val errores: NuevoProductoErrores = NuevoProductoErrores(),
    val guardando: Boolean = false
)
