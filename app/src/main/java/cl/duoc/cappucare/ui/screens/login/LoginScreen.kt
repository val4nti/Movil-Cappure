package cl.duoc.cappucare.ui.screens.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.ui.components.CampoTexto
import cl.duoc.cappucare.viewmodel.SesionViewModel

/**
 * Pantalla de inicio de sesión: usuario, contraseña y perfil.
 * Las reglas de validación están en [SesionViewModel.ingresar].
 *
 * @param onIngresoExitoso Se llama cuando el formulario es válido, para ir al inventario.
 */
@Composable
fun LoginScreen(
    viewModel: SesionViewModel,
    onIngresoExitoso: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.login.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Cappucare",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Control de existencias de insumos médicos",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        CampoTexto(
            valor = state.usuario,
            onChange = viewModel::onUsuarioChanged,
            etiqueta = "Usuario",
            error = state.errores.usuario,
            icono = Icons.Filled.Person
        )

        CampoTexto(
            valor = state.contrasena,
            onChange = viewModel::onContrasenaChanged,
            etiqueta = "Contraseña",
            error = state.errores.contrasena,
            icono = Icons.Filled.Lock,
            tipoTeclado = KeyboardType.Password,
            // Oculta los caracteres salvo que el usuario toque el ojo
            transformacion = if (state.mostrarContrasena) VisualTransformation.None
            else PasswordVisualTransformation(),
            accionFinal = {
                IconButton(onClick = viewModel::onMostrarContrasena) {
                    Icon(
                        imageVector = if (state.mostrarContrasena) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = if (state.mostrarContrasena) "Ocultar contraseña"
                        else "Mostrar contraseña"
                    )
                }
            }
        )

        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleSmall,
            color = if (state.errores.perfil != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
        Perfil.entries.forEach { perfil ->
            TarjetaPerfil(
                perfil = perfil,
                seleccionado = state.perfil == perfil,
                conError = state.errores.perfil != null,
                onClick = { viewModel.onPerfilChanged(perfil) }
            )
        }
        state.errores.perfil?.let { error ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Error, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                Text(" $error", color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { if (viewModel.ingresar()) onIngresoExitoso() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Ingresar")
        }

        Text(
            text = "Prototipo académico · datos ficticios",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/** Tarjeta seleccionable para un perfil. El borde se anima al seleccionarla. */
@Composable
private fun TarjetaPerfil(
    perfil: Perfil,
    seleccionado: Boolean,
    conError: Boolean,
    onClick: () -> Unit
) {
    val colorBorde by animateColorAsState(
        targetValue = when {
            seleccionado -> MaterialTheme.colorScheme.primary
            conError -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "bordePerfil"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, colorBorde),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = seleccionado, onClick = onClick)
            Column {
                Text(perfil.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    perfil.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
