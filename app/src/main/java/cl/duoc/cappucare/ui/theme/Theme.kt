package cl.duoc.cappucare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal20,
    background = Fondo,
    surfaceContainerLow = Fondo
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    primaryContainer = Teal20,
    onPrimaryContainer = Teal90
)

/**
 * Tema Material 3 de Cappucare.
 * No se usa color dinámico para que la app mantenga siempre los colores de la marca.
 */
@Composable
fun CappucareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
