package cl.duoc.cappucare.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests de las reglas de validación de los formularios.
 * Cada regla devuelve null si el dato es válido o el mensaje de error si no lo es.
 */
class ValidacionesTest {

    @Test
    fun `usuario valido no tiene error`() = assertNull(Validaciones.usuario("juan"))

    @Test
    fun `usuario vacio con espacios o corto da error`() {
        assertEquals("El usuario es obligatorio", Validaciones.usuario(""))
        assertEquals("El usuario no puede tener espacios", Validaciones.usuario("juan perez"))
        assertEquals("Debe tener al menos 3 caracteres", Validaciones.usuario("ju"))
    }

    @Test
    fun `contrasena valida e invalida`() {
        assertNull(Validaciones.contrasena("1234"))
        assertEquals("La contraseña es obligatoria", Validaciones.contrasena(""))
        assertEquals("Debe tener al menos 4 caracteres", Validaciones.contrasena("123"))
    }

    @Test
    fun `codigo debe tener 13 digitos numericos`() {
        assertNull(Validaciones.codigo("7801234000112"))
        assertEquals("El código es obligatorio", Validaciones.codigo(""))
        assertEquals("Solo se permiten números", Validaciones.codigo("78012340001AB"))
        assertEquals("Debe tener 13 dígitos", Validaciones.codigo("123"))
    }

    @Test
    fun `nombre obligatorio entre 3 y 60 caracteres`() {
        assertNull(Validaciones.nombre("Jeringa 5 ml"))
        assertEquals("El nombre es obligatorio", Validaciones.nombre("  "))
        assertEquals("Debe tener al menos 3 caracteres", Validaciones.nombre("ab"))
        assertEquals("Máximo 60 caracteres", Validaciones.nombre("a".repeat(61)))
    }

    @Test
    fun `descripcion maximo 200 caracteres`() {
        assertNull(Validaciones.descripcion(""))
        assertEquals("Máximo 200 caracteres", Validaciones.descripcion("a".repeat(201)))
    }

    @Test
    fun `campo obligatorio y ubicacion`() {
        assertNull(Validaciones.obligatorio("Higiene", "una categoría"))
        assertEquals("Selecciona una categoría", Validaciones.obligatorio(null, "una categoría"))
        assertNull(Validaciones.ubicacion("Bodega A"))
        assertEquals("La ubicación es obligatoria", Validaciones.ubicacion(""))
    }

    @Test
    fun `entero valida vacio, texto y minimo`() {
        assertNull(Validaciones.entero("5", 1, "La cantidad"))
        assertEquals("La cantidad es obligatorio", Validaciones.entero("", 1, "La cantidad"))
        assertEquals("Ingresa un número entero", Validaciones.entero("abc", 1, "La cantidad"))
        assertEquals("El mínimo debe ser 1 o más", Validaciones.entero("0", 1, "El mínimo"))
    }

    @Test
    fun `salida no puede superar el stock pero un ingreso si`() {
        assertNull(Validaciones.cantidadMovimiento("5", esSalida = true, stockActual = 10))
        assertEquals("Solo hay 10 disponibles", Validaciones.cantidadMovimiento("11", esSalida = true, stockActual = 10))
        assertNull(Validaciones.cantidadMovimiento("50", esSalida = false, stockActual = 10))
        assertEquals("Ingresa un número entero", Validaciones.cantidadMovimiento("x", esSalida = true, stockActual = 10))
    }
}
