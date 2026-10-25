package cl.duoc.cappucare.utils

/**
 * Reglas de validación de los formularios de la app.
 *
 * Cada función recibe el valor del campo y devuelve:
 * - null si el valor es válido.
 * - Un mensaje de error si no lo es (se muestra bajo el campo).
 *
 * Al estar separadas de la UI, las usan los ViewModels y se pueden probar sin pantalla.
 * Para agregar una regla nueva basta con crear otra función aquí y llamarla desde el ViewModel.
 */
object Validaciones {

    const val LARGO_CODIGO = 13

    fun usuario(valor: String): String? = when {
        valor.isBlank() -> "El usuario es obligatorio"
        valor.contains(" ") -> "El usuario no puede tener espacios"
        valor.length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun contrasena(valor: String): String? = when {
        valor.isBlank() -> "La contraseña es obligatoria"
        valor.length < 4 -> "Debe tener al menos 4 caracteres"
        else -> null
    }

    fun codigo(valor: String): String? = when {
        valor.isBlank() -> "El código es obligatorio"
        !valor.all { it.isDigit() } -> "Solo se permiten números"
        valor.length != LARGO_CODIGO -> "Debe tener $LARGO_CODIGO dígitos"
        else -> null
    }

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        valor.length > 60 -> "Máximo 60 caracteres"
        else -> null
    }

    fun descripcion(valor: String): String? =
        if (valor.length > 200) "Máximo 200 caracteres" else null

    fun obligatorio(valor: String?, campo: String): String? =
        if (valor.isNullOrBlank()) "Selecciona $campo" else null

    fun ubicacion(valor: String): String? =
        if (valor.isBlank()) "La ubicación es obligatoria" else null

    /** Número entero mayor o igual a [minimo]. */
    fun entero(valor: String, minimo: Int, campo: String): String? {
        val numero = valor.toIntOrNull()
        return when {
            valor.isBlank() -> "$campo es obligatorio"
            numero == null -> "Ingresa un número entero"
            numero < minimo -> "$campo debe ser $minimo o más"
            else -> null
        }
    }

    /** Cantidad de un movimiento: entero mayor que 0 y, si es salida, no mayor que el stock. */
    fun cantidadMovimiento(valor: String, esSalida: Boolean, stockActual: Int): String? {
        entero(valor, 1, "La cantidad")?.let { return it }
        val numero = valor.toInt()
        return if (esSalida && numero > stockActual) "Solo hay $stockActual disponibles" else null
    }
}
