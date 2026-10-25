package cl.duoc.cappucare.model

import cl.duoc.cappucare.data.local.entity.ProductoEntity

/** Motivos posibles según el tipo de movimiento. */
fun motivosPara(tipo: TipoMovimiento): List<String> = when (tipo) {
    TipoMovimiento.INGRESO -> listOf("Recepción", "Devolución", "Ajuste")
    TipoMovimiento.SALIDA -> listOf("Venta", "Merma", "Ajuste")
}

/** Mensajes de error del formulario de movimiento (null = sin error). */
data class MovimientoErrores(
    val producto: String? = null,
    val cantidad: String? = null,
    val motivo: String? = null
)

/** Datos que el usuario va llenando en el formulario. */
data class FormularioMovimiento(
    val productoId: Long = -1L,
    val tipo: TipoMovimiento = TipoMovimiento.SALIDA,
    val cantidad: String = "",
    val motivo: String? = motivosPara(TipoMovimiento.SALIDA).first(),
    val soloSalidas: Boolean = false,       // true para el perfil Vendedor
    val errores: MovimientoErrores = MovimientoErrores(),
    val guardando: Boolean = false,
    val mensajeExito: String? = null
)

/**
 * Estado completo de la pantalla de registrar movimiento.
 * Junta el formulario con la lista de productos que viene de Room.
 */
data class MovimientoUiState(
    val productos: List<ProductoEntity> = emptyList(),
    val producto: ProductoEntity? = null,
    val formulario: FormularioMovimiento = FormularioMovimiento()
) {
    /** Cantidad escrita como número, o null si no es un número válido mayor que 0. */
    private val cantidadNumero: Int? get() = formulario.cantidad.toIntOrNull()?.takeIf { it > 0 }

    /** Stock que quedará después del movimiento (se muestra en vivo mientras se escribe). */
    val stockResultante: Int?
        get() {
            val actual = producto?.cantidad ?: return null
            val cantidad = cantidadNumero ?: return null
            return when (formulario.tipo) {
                TipoMovimiento.INGRESO -> actual + cantidad
                TipoMovimiento.SALIDA -> actual - cantidad
            }
        }

    /** true si el stock resultante queda bajo el nivel mínimo del producto. */
    val quedaBajoMinimo: Boolean
        get() {
            val resultante = stockResultante ?: return false
            val minimo = producto?.stockMinimo ?: return false
            return resultante < minimo
        }
}
