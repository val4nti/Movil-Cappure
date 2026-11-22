package cl.duoc.cappucare.data.remote

import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.data.remote.dto.MovimientoDto
import cl.duoc.cappucare.data.remote.dto.ProductoDto
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.model.UnidadMedida
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

/** Tests de las conversiones entre lo que viaja por la red (DTO) y lo que guarda Room (Entity). */
class MapeosTest {

    private val producto = ProductoEntity(
        id = 0, codigo = "7801234000112", nombre = "Guantes", descripcion = "Nitrilo",
        categoria = "Protección", cantidad = 12, unidad = UnidadMedida.CAJA,
        stockMinimo = 20, ubicacion = "Bodega A"
    )

    @Test
    fun `producto nuevo se envia sin id y con la unidad como texto`() {
        val dto = producto.aDto()
        assertNull(dto.id)                  // el id lo pone MySQL
        assertEquals("CAJA", dto.unidad)
        assertEquals(12, dto.cantidad)
    }

    @Test
    fun `producto existente conserva su id`() {
        assertEquals(5L, producto.copy(id = 5).aDto().id)
    }

    @Test
    fun `dto del servidor se convierte en entity conservando la foto local`() {
        val dto = ProductoDto(3, "7801234000136", "Jeringa", null, "Material clínico", 85, "UNIDAD", 50, null, null)
        val entity = dto.aEntity(fotoUri = "content://foto")
        assertEquals(3L, entity.id)
        assertEquals(UnidadMedida.UNIDAD, entity.unidad)
        assertEquals("", entity.descripcion)    // null del servidor → texto vacío
        assertEquals("", entity.ubicacion)
        assertTrue(entity.activo)
        assertEquals("content://foto", entity.fotoUri)
    }

    @Test
    fun `movimiento se envia con el tipo como texto`() {
        val dto = MovimientoEntity(productoId = 1, tipo = TipoMovimiento.SALIDA, cantidad = 5,
            motivo = "Venta", perfil = "Vendedor").aDto()
        assertEquals("SALIDA", dto.tipo)
        assertNull(dto.fecha)                    // la fecha la pone el servidor
    }

    @Test
    fun `movimiento del servidor convierte tipo y fecha`() {
        val dto = MovimientoDto(7, 1, "INGRESO", 10, "Recepción", "Encargado de bodega", "2026-11-22T18:13:12.320495")
        val entity = dto.aEntity()
        assertEquals(7L, entity.id)
        assertEquals(TipoMovimiento.INGRESO, entity.tipo)
        val esperado = LocalDateTime.of(2026, 11, 22, 18, 13, 12, 320495000)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(esperado, entity.fecha)
    }

    @Test
    fun `movimiento sin fecha usa la hora actual`() {
        val antes = System.currentTimeMillis()
        val entity = MovimientoDto(null, 1, "SALIDA", 1, "Merma", "Vendedor", null).aEntity()
        assertEquals(0L, entity.id)
        assertTrue(entity.fecha >= antes)
    }
}
