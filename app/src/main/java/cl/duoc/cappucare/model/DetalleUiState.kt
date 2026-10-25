package cl.duoc.cappucare.model

import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity

/** Estado de la pantalla de detalle de producto. */
data class DetalleUiState(
    val cargando: Boolean = true,
    val producto: ProductoEntity? = null,
    val ultimoMovimiento: MovimientoEntity? = null
)
