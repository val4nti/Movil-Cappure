package cl.duoc.cappucare.model

/**
 * Tipo de movimiento que modifica las existencias de un producto.
 * - INGRESO suma unidades (ej. recepción de mercadería).
 * - SALIDA resta unidades (ej. venta, merma o ajuste).
 */
enum class TipoMovimiento(val etiqueta: String) {
    INGRESO("Ingreso"),
    SALIDA("Salida")
}
