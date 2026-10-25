package cl.duoc.cappucare.navigation

import cl.duoc.cappucare.model.TipoMovimiento

/**
 * Rutas de navegación de la app. Se usa una sealed class para que cada ruta exista
 * una sola vez y no se escriban textos a mano en distintas partes del código.
 *
 * Las rutas con parámetros tienen una función crear(...) que arma la ruta real.
 */
sealed class Ruta(val ruta: String) {

    data object Login : Ruta("login")

    data object Inventario : Ruta("inventario")

    data object Detalle : Ruta("detalle/{productoId}") {
        fun crear(productoId: Long) = "detalle/$productoId"
    }

    /** productoId = -1 significa "sin producto elegido". tipo vacío significa "sin tipo elegido". */
    data object Movimiento : Ruta("movimiento?productoId={productoId}&tipo={tipo}") {
        fun crear(productoId: Long = -1L, tipo: TipoMovimiento? = null) =
            "movimiento?productoId=$productoId&tipo=${tipo?.name ?: ""}"
    }

    /** productoId = -1 muestra el historial de todos los productos. */
    data object Historial : Ruta("historial?productoId={productoId}") {
        fun crear(productoId: Long = -1L) = "historial?productoId=$productoId"
    }

    data object Alertas : Ruta("alertas")

    data object NuevoProducto : Ruta("nuevo_producto?codigo={codigo}") {
        fun crear(codigo: String = "") = "nuevo_producto?codigo=$codigo"
    }

    data object BuscarCodigo : Ruta("buscar_codigo")
}

/** Nombres de los argumentos de las rutas. */
object Argumentos {
    const val PRODUCTO_ID = "productoId"
    const val TIPO = "tipo"
    const val CODIGO = "codigo"
}
