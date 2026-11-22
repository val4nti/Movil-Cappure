package cl.duoc.cappucare.model

/** Valores del día que se muestran en la app (ya convertidos desde el JSON de mindicador.cl). */
data class Indicadores(
    val dolar: Double,
    val uf: Double,
    val fecha: String          // "2026-11-22"
)

/** Estado de la tarjeta de indicadores en el inventario. */
data class IndicadoresUiState(
    val cargando: Boolean = true,
    val indicadores: Indicadores? = null,
    val error: String? = null
)
