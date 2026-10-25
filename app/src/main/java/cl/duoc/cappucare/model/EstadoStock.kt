package cl.duoc.cappucare.model

/**
 * Estado de disponibilidad de un producto según su cantidad y su nivel mínimo.
 * No se guarda en la base de datos: se calcula cada vez a partir de la cantidad.
 */
enum class EstadoStock(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    STOCK_BAJO("Stock bajo"),
    AGOTADO("Agotado");

    companion object {
        /**
         * Calcula el estado de un producto.
         * - Agotado: no quedan unidades.
         * - Stock bajo: quedan menos unidades que el nivel mínimo.
         * - Disponible: en cualquier otro caso.
         */
        fun calcular(cantidad: Int, stockMinimo: Int): EstadoStock = when {
            cantidad <= 0 -> AGOTADO
            cantidad < stockMinimo -> STOCK_BAJO
            else -> DISPONIBLE
        }
    }
}
