package cl.duoc.cappucare.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val locale = Locale.forLanguageTag("es-CL")

/** Ej. "12 oct 2026". */
fun Long.comoFecha(): String =
    SimpleDateFormat("dd MMM yyyy", locale).format(Date(this)).replace(".", "")

/** Ej. "12 oct". */
fun Long.comoFechaCorta(): String =
    SimpleDateFormat("dd MMM", locale).format(Date(this)).replace(".", "")

/**
 * Formato de pesos chilenos con 2 decimales. Ej. 39876.5 → "$39.876,50".
 * Se arma con Locale.US y después se intercambian "," y "." para que el resultado
 * sea igual en cualquier teléfono.
 */
fun Double.comoPesos(): String {
    val us = String.format(Locale.US, "%,.2f", this)   // "39,876.50"
    return "$" + us.replace(",", "_").replace(".", ",").replace("_", ".")
}
