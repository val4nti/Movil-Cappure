package cl.duoc.cappucare.model

/**
 * Unidades de medida en que se controla cada producto.
 *
 * @property nombre Texto que se muestra en formularios y en el detalle (ej. "Caja").
 * @property abreviatura Texto corto que acompaña a la cantidad en los listados (ej. "12 cajas").
 */
enum class UnidadMedida(val nombre: String, val abreviatura: String) {
    UNIDAD("Unidad", "un."),
    CAJA("Caja", "cajas"),
    PAQUETE("Paquete", "paq.")
}
