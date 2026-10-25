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
