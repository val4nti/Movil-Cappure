package cl.duoc.cappucare.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class FormatoTest {

    @Test
    fun `pesos usa punto para miles y coma para decimales`() {
        assertEquals("$39.876,50", 39876.5.comoPesos())
        assertEquals("$948,12", 948.12.comoPesos())
        assertEquals("$1.000.000,00", 1_000_000.0.comoPesos())
    }

    @Test
    fun `fechas incluyen dia y anio`() {
        val millis = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 12) }.timeInMillis
        assertTrue(millis.comoFecha().startsWith("12"))
        assertTrue(millis.comoFecha().endsWith("2026"))
        assertTrue(millis.comoFechaCorta().startsWith("12"))
    }
}
