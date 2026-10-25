package cl.duoc.cappucare.model

import cl.duoc.cappucare.data.local.entity.MovimientoConProducto

/** Filtros del historial. */
enum class FiltroHistorial(val etiqueta: String) {
    TODOS("Todos"),
    INGRESOS("Ingresos"),
    SALIDAS("Salidas")
}

/**
 * Estado de la pantalla de historial.
 *
 * @property nombreProducto Si el historial es de un solo producto, su nombre; si no, null.
 */
data class HistorialUiState(
    val movimientos: List<MovimientoConProducto> = emptyList(),
    val filtro: FiltroHistorial = FiltroHistorial.TODOS,
    val nombreProducto: String? = null,
    val cargando: Boolean = true
)
