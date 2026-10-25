package cl.duoc.cappucare.data.local

import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.model.UnidadMedida
import java.util.Calendar

/**
 * Catálogo simulado de productos que se carga la primera vez que se abre la app.
 * Todos los datos son ficticios, como exige el caso.
 */
object DatosIniciales {

    val productos = listOf(
        ProductoEntity(
            codigo = "7801234000112",
            nombre = "Guantes de nitrilo talla M",
            descripcion = "Guantes de nitrilo sin polvo, para uso clínico. Caja de 100 unidades.",
            categoria = "Protección",
            cantidad = 12,
            unidad = UnidadMedida.CAJA,
            stockMinimo = 20,
            ubicacion = "Bodega A · Estante 3"
        ),
        ProductoEntity(
            codigo = "7801234000129",
            nombre = "Mascarillas quirúrgicas",
            descripcion = "Mascarilla de tres pliegues con elástico.",
            categoria = "Protección",
            cantidad = 340,
            unidad = UnidadMedida.UNIDAD,
            stockMinimo = 100,
            ubicacion = "Bodega A · Estante 1"
        ),
        ProductoEntity(
            codigo = "7801234000136",
            nombre = "Jeringa 5 ml",
            descripcion = "Jeringa desechable estéril con aguja.",
            categoria = "Material clínico",
            cantidad = 85,
            unidad = UnidadMedida.UNIDAD,
            stockMinimo = 50,
            ubicacion = "Bodega B · Estante 2"
        ),
        ProductoEntity(
            codigo = "7801234000143",
            nombre = "Alcohol gel 500 ml",
            descripcion = "Alcohol gel al 70% con dosificador.",
            categoria = "Higiene",
            cantidad = 0,
            unidad = UnidadMedida.UNIDAD,
            stockMinimo = 15,
            ubicacion = "Bodega A · Estante 4"
        ),
        ProductoEntity(
            codigo = "7801234000150",
            nombre = "Gasas estériles 10×10",
            descripcion = "Paquete de gasas de algodón estériles.",
            categoria = "Curación",
            cantidad = 18,
            unidad = UnidadMedida.PAQUETE,
            stockMinimo = 30,
            ubicacion = "Bodega B · Estante 1"
        ),
        ProductoEntity(
            codigo = "7801234000167",
            nombre = "Termómetro digital",
            descripcion = "Termómetro digital de punta flexible.",
            categoria = "Equipos",
            cantidad = 22,
            unidad = UnidadMedida.UNIDAD,
            stockMinimo = 10,
            ubicacion = "Bodega C · Vitrina"
        ),
        ProductoEntity(
            codigo = "7801234000174",
            nombre = "Apósito transparente",
            descripcion = "Apósito adhesivo transparente de 6×7 cm.",
            categoria = "Curación",
            cantidad = 9,
            unidad = UnidadMedida.UNIDAD,
            stockMinimo = 25,
            ubicacion = "Bodega B · Estante 1"
        )
    )

    /**
     * Historial ficticio de movimientos, igual al del mockup.
     *
     * @param ids Ids que Room generó al insertar [productos], en el mismo orden.
     */
    fun movimientos(ids: List<Long>): List<MovimientoEntity> {
        val perfil = "Encargado de bodega"
        return listOf(
            MovimientoEntity(productoId = ids[0], tipo = TipoMovimiento.SALIDA, cantidad = 5, motivo = "Venta", perfil = perfil, fecha = fecha(12)),
            MovimientoEntity(productoId = ids[1], tipo = TipoMovimiento.INGRESO, cantidad = 200, motivo = "Recepción", perfil = perfil, fecha = fecha(11)),
            MovimientoEntity(productoId = ids[3], tipo = TipoMovimiento.SALIDA, cantidad = 15, motivo = "Venta", perfil = perfil, fecha = fecha(11)),
            MovimientoEntity(productoId = ids[4], tipo = TipoMovimiento.SALIDA, cantidad = 12, motivo = "Venta", perfil = perfil, fecha = fecha(10)),
            MovimientoEntity(productoId = ids[2], tipo = TipoMovimiento.INGRESO, cantidad = 100, motivo = "Recepción", perfil = perfil, fecha = fecha(9)),
            MovimientoEntity(productoId = ids[6], tipo = TipoMovimiento.SALIDA, cantidad = 4, motivo = "Merma", perfil = perfil, fecha = fecha(9)),
            MovimientoEntity(productoId = ids[5], tipo = TipoMovimiento.INGRESO, cantidad = 10, motivo = "Recepción", perfil = perfil, fecha = fecha(8))
        )
    }

    /** Fecha en milisegundos para un día de octubre de 2026 a las 10:00. */
    private fun fecha(dia: Int): Long = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, dia, 10, 0, 0)
    }.timeInMillis
}
