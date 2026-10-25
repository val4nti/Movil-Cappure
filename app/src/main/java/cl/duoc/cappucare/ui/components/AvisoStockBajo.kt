package cl.duoc.cappucare.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.cappucare.ui.theme.StockBajoFondo
import cl.duoc.cappucare.ui.theme.StockBajoTexto

/**
 * Aviso amarillo que indica cuántos productos están bajo el mínimo,
 * con un botón "Ver" para filtrarlos.
 */
@Composable
fun AvisoStockBajo(
    cantidad: Int,
    onVer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = StockBajoFondo,
            contentColor = StockBajoTexto
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (cantidad == 1) "1 producto bajo el mínimo"
                else "$cantidad productos bajo el mínimo",
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onVer) { Text("Ver", color = StockBajoTexto) }
        }
    }
}
