package cl.duoc.cappucare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Lista desplegable con estilo de campo de texto (ej. elegir producto o categoría).
 *
 * Usa [CampoTexto] de solo lectura y, encima, una capa que al tocarla abre el menú.
 * El único estado local es si el menú está abierto, que es un detalle visual.
 */
@Composable
fun <T> SelectorDesplegable(
    etiqueta: String,
    opciones: List<T>,
    seleccionada: T?,
    onSeleccion: (T) -> Unit,
    texto: (T) -> String,
    error: String?,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    ayuda: String? = null
) {
    var abierto by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        CampoTexto(
            valor = seleccionada?.let(texto).orEmpty(),
            onChange = {},
            etiqueta = etiqueta,
            error = error,
            icono = icono,
            ayuda = ayuda,
            accionFinal = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
        )
        // Capa transparente que recibe el toque (el campo es solo de lectura)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { abierto = true }
        )
        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(texto(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}
