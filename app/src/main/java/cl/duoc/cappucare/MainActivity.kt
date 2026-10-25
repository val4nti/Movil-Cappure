package cl.duoc.cappucare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.cappucare.model.EstadoSesion
import cl.duoc.cappucare.navigation.AppNavigation
import cl.duoc.cappucare.ui.screens.carga.CargaScreen
import cl.duoc.cappucare.ui.theme.CappucareTheme
import cl.duoc.cappucare.utils.Notificaciones
import cl.duoc.cappucare.viewmodel.SesionViewModel

/**
 * Actividad principal (única) de la app.
 *
 * 1. Crea el canal de notificaciones.
 * 2. Muestra una pantalla de carga mientras se lee la sesión guardada.
 * 3. Luego entrega el control a [AppNavigation].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notificaciones.crearCanal(this)
        enableEdgeToEdge()
        setContent {
            CappucareTheme {
                // ViewModel compartido: vive mientras viva la actividad
                val sesionViewModel: SesionViewModel = viewModel()
                val estado by sesionViewModel.estadoSesion.collectAsState()

                // Al iniciar o cerrar sesión cambia la rama y la navegación se crea de nuevo,
                // partiendo en el inventario (con sesión) o en el login (sin sesión)
                when (val actual = estado) {
                    EstadoSesion.Cargando -> CargaScreen()
                    is EstadoSesion.Activa -> AppNavigation(sesionViewModel, actual.sesion)
                    EstadoSesion.Inactiva -> AppNavigation(sesionViewModel, null)
                }
            }
        }
    }
}
