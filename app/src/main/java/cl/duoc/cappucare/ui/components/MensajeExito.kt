package cl.duoc.cappucare.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.cappucare.ui.theme.DisponibleFondo
import cl.duoc.cappucare.ui.theme.DisponibleTexto
import kotlinx.coroutines.delay

/**
 * Tarjeta verde de confirmación que entra deslizándose y se oculta sola después de unos segundos.
 *
 * @param mensaje Texto a mostrar; null la oculta.
 * @param onOcultar Se llama cuando pasa el tiempo, para que el ViewModel limpie el mensaje.
 */
@Composable
fun MensajeExito(
    mensaje: String?,
    onOcultar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Se recuerda el último texto para que no desaparezca mientras corre la animación de salida
    var ultimoMensaje by remember { mutableStateOf("") }
    if (mensaje != null) ultimoMensaje = mensaje

    // Cada vez que llega un mensaje nuevo, se espera y luego se oculta
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            delay(2500)
            onOcultar()
        }
    }

    AnimatedVisibility(
        visible = mensaje != null,
        enter = slideInVertically { -it } + fadeIn() + scaleIn(initialScale = 0.9f),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DisponibleFondo, contentColor = DisponibleTexto)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(ultimoMensaje)
            }
        }
    }
}
