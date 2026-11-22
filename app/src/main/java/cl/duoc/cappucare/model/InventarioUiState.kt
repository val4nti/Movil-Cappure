package cl.duoc.cappucare.model

import cl.duoc.cappucare.data.local.entity.ProductoEntity

/** Filtros disponibles en la pantalla de inventario. */
enum class FiltroInventario {
    TODOS,
    STOCK_BAJO
}

/**
 * Estado inmutable de la pantalla de inventario.
 * El ViewModel crea una copia nueva cada vez que algo cambia y la UI se recompone.
 *
 * @property productos Productos que se muestran, ya filtrados por búsqueda y filtro.
 * @property busqueda Texto escrito en el buscador.
 * @property filtro Filtro seleccionado.
 * @property totalProductos Cantidad total de productos (para el chip "Todos").
 * @property totalStockBajo Productos agotados o bajo el mínimo (para el chip y el aviso).
 * @property cargando true mientras se leen los datos por primera vez.
 * @property sincronizando true mientras se descargan los datos de los microservicios.
 * @property errorConexion mensaje si no se pudo hablar con los microservicios (null = todo bien).
 */
data class InventarioUiState(
    val productos: List<ProductoEntity> = emptyList(),
    val busqueda: String = "",
    val filtro: FiltroInventario = FiltroInventario.TODOS,
    val totalProductos: Int = 0,
    val totalStockBajo: Int = 0,
    val cargando: Boolean = true,
    val sincronizando: Boolean = false,
    val errorConexion: String? = null
)
