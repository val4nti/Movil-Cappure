package cl.duoc.cappucare.ui.screens.producto

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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.model.CATEGORIAS
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.UnidadMedida
import cl.duoc.cappucare.ui.components.BarraSuperior
import cl.duoc.cappucare.ui.components.CampoTexto
import cl.duoc.cappucare.ui.components.SelectorDesplegable
import cl.duoc.cappucare.ui.components.SelectorOpciones
import cl.duoc.cappucare.utils.Validaciones
import cl.duoc.cappucare.viewmodel.NuevoProductoViewModel

/**
 * Formulario para agregar un producto al inventario.
 * Cada campo muestra su error con ícono y mensaje; las reglas están en [Validaciones].
 *
 * @param onGuardado Se llama con el id del producto creado.
 */
@Composable
fun NuevoProductoScreen(
    perfil: Perfil,
    onVolver: () -> Unit,
    onGuardado: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NuevoProductoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val e = state.errores

    Column(modifier = modifier.fillMaxSize()) {
        BarraSuperior(titulo = "Nuevo producto", onVolver = onVolver)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CampoTexto(
                valor = state.codigo,
                onChange = viewModel::onCodigoChanged,
                etiqueta = "Código de barras",
                error = e.codigo,
                icono = Icons.Filled.QrCode,
                ayuda = "Escribe los ${Validaciones.LARGO_CODIGO} dígitos impresos en el envase " +
                    "(${state.codigo.length}/${Validaciones.LARGO_CODIGO})",
                tipoTeclado = KeyboardType.Number
            )
            CampoTexto(
                valor = state.nombre,
                onChange = viewModel::onNombreChanged,
                etiqueta = "Nombre",
                error = e.nombre,
                icono = Icons.AutoMirrored.Filled.Label
            )
            CampoTexto(
                valor = state.descripcion,
                onChange = viewModel::onDescripcionChanged,
                etiqueta = "Descripción (opcional)",
                error = e.descripcion,
                icono = Icons.AutoMirrored.Filled.Notes,
                ayuda = "${state.descripcion.length}/200",
                unaLinea = false
            )
            SelectorDesplegable(
                etiqueta = "Categoría",
                opciones = CATEGORIAS,
                seleccionada = state.categoria,
                onSeleccion = viewModel::onCategoriaChanged,
                texto = { it },
                error = e.categoria,
                icono = Icons.Filled.Category
            )
            SelectorOpciones(
                titulo = "Unidad de medida",
                opciones = UnidadMedida.entries,
                seleccionada = state.unidad,
                onSeleccion = viewModel::onUnidadChanged,
                texto = { it.nombre },
                error = e.unidad,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoTexto(
                    valor = state.cantidad,
                    onChange = viewModel::onCantidadChanged,
                    etiqueta = "Cantidad inicial",
                    error = e.cantidad,
                    icono = Icons.Filled.Numbers,
                    tipoTeclado = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                CampoTexto(
                    valor = state.minimo,
                    onChange = viewModel::onMinimoChanged,
                    etiqueta = "Nivel mínimo",
                    error = e.minimo,
                    icono = Icons.Filled.Warning,
                    tipoTeclado = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            CampoTexto(
                valor = state.ubicacion,
                onChange = viewModel::onUbicacionChanged,
                etiqueta = "Ubicación de almacenamiento",
                error = e.ubicacion,
                icono = Icons.Filled.LocationOn,
                ayuda = "Ej. Bodega A · Estante 3"
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.guardar(perfil, onGuardado) },
                enabled = !state.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar producto")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
