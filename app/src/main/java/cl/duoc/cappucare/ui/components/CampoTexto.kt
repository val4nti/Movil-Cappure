package cl.duoc.cappucare.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Campo de texto reutilizable con validación visual.
 *
 * Es stateless (state hoisting): recibe el [valor] y avisa los cambios con [onChange].
 * Cuando [error] no es null:
 * - el borde se pinta de rojo (isError),
 * - aparece un ícono de error al final del campo,
 * - el mensaje se muestra debajo (supportingText).
 *
 * @param ayuda Texto gris que se muestra debajo cuando no hay error (opcional).
 * @param accionFinal Ícono o botón al final del campo cuando no hay error (ej. ver contraseña).
 */
@Composable
fun CampoTexto(
    valor: String,
    onChange: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    ayuda: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    transformacion: VisualTransformation = VisualTransformation.None,
    unaLinea: Boolean = true,
    accionFinal: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(),
        isError = error != null,
        leadingIcon = if (icono != null) {
            { Icon(icono, contentDescription = null) }
        } else null,
        trailingIcon = when {
            error != null -> {
                { Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) }
            }
            else -> accionFinal
        },
        supportingText = when {
            error != null -> { { Text(error, color = MaterialTheme.colorScheme.error) } }
            ayuda != null -> { { Text(ayuda) } }
            else -> null
        },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        visualTransformation = transformacion,
        singleLine = unaLinea,
        minLines = if (unaLinea) 1 else 2
    )
}
