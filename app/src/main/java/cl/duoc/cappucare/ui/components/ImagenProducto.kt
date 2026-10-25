package cl.duoc.cappucare.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Foto del producto con bordes redondeados (equivalente a ImagenInteligente de la guía 13).
 *
 * - Si hay foto, la carga con Coil ([AsyncImage]) recortada para llenar el cuadro.
 * - Si no hay, muestra un ícono por defecto.
 * - [Crossfade] anima el cambio entre ambos casos al tomar o quitar la foto.
 */
@Composable
fun ImagenProducto(
    fotoUri: String?,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp
) {
    val forma = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .size(tamano)
            .clip(forma)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), forma),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = fotoUri, label = "foto") { uri ->
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Foto del producto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Inventory2,
                        contentDescription = "Sin foto",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(tamano / 2.5f)
                    )
                }
            }
        }
    }
}
