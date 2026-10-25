package cl.duoc.cappucare.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.Sesion
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.ui.screens.alertas.AlertasScreen
import cl.duoc.cappucare.ui.screens.codigo.BuscarCodigoScreen
import cl.duoc.cappucare.ui.screens.detalle.DetalleProductoScreen
import cl.duoc.cappucare.ui.screens.historial.HistorialScreen
import cl.duoc.cappucare.ui.screens.inventario.InventarioScreen
import cl.duoc.cappucare.ui.screens.login.LoginScreen
import cl.duoc.cappucare.ui.screens.movimiento.MovimientoScreen
import cl.duoc.cappucare.ui.screens.producto.NuevoProductoScreen
import cl.duoc.cappucare.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/** Pestañas de la barra inferior. */
private data class Pestana(val titulo: String, val icono: ImageVector, val patron: String, val destino: () -> String)

private val pestanas = listOf(
    Pestana("Inventario", Icons.Filled.Inventory2, Ruta.Inventario.ruta) { Ruta.Inventario.ruta },
    Pestana("Movimiento", Icons.Filled.SwapVert, Ruta.Movimiento.ruta) { Ruta.Movimiento.crear() },
    Pestana("Historial", Icons.Filled.History, Ruta.Historial.ruta) { Ruta.Historial.crear() }
)

/**
 * Navegación completa de la app: el NavHost con todas las pantallas, la barra inferior
 * y el Snackbar para mensajes.
 *
 * @param sesion Sesión actual (null si no hay nadie conectado).
 */
@Composable
fun AppNavigation(
    sesionViewModel: SesionViewModel,
    sesion: Sesion?
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val mostrarMensaje: (String) -> Unit = { texto ->
        scope.launch { snackbarHostState.showSnackbar(texto) }
    }

    // La pantalla inicial se decide una sola vez: si ya hay sesión guardada se salta el login
    val inicio = remember { if (sesion == null) Ruta.Login.ruta else Ruta.Inventario.ruta }

    val perfil = sesion?.perfil ?: Perfil.VENDEDOR
    val esEncargado = perfil == Perfil.ENCARGADO

    // Ruta actual, para saber qué pestaña marcar y si mostrar la barra inferior
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarBarra = pestanas.any { it.patron == rutaActual }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = mostrarBarra,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = rutaActual == pestana.patron,
                            onClick = { navController.irAPestana(pestana.destino()) },
                            icon = { Icon(pestana.icono, contentDescription = null) },
                            label = { Text(pestana.titulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = inicio,
            modifier = Modifier.padding(innerPadding),
            // Animaciones al cambiar de pantalla
            enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 10 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(250)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 10 } }
        ) {
            composable(Ruta.Login.ruta) {
                LoginScreen(
                    viewModel = sesionViewModel,
                    onIngresoExitoso = {
                        navController.navigate(Ruta.Inventario.ruta) {
                            popUpTo(Ruta.Login.ruta) { inclusive = true }  // No se vuelve al login con "atrás"
                        }
                    }
                )
            }

            composable(Ruta.Inventario.ruta) {
                InventarioScreen(
                    usuario = sesion?.usuario.orEmpty(),
                    puedeCrear = esEncargado,
                    onProductoClick = { id -> navController.navigate(Ruta.Detalle.crear(id)) },
                    onNuevoProducto = { navController.navigate(Ruta.NuevoProducto.crear()) },
                    onAlertas = { navController.navigate(Ruta.Alertas.ruta) },
                    onBuscarCodigo = { navController.navigate(Ruta.BuscarCodigo.ruta) },
                    onCerrarSesion = {
                        sesionViewModel.cerrarSesion()
                        navController.navigate(Ruta.Login.ruta) {
                            popUpTo(navController.graph.id) { inclusive = true }  // Limpia todo el historial
                        }
                    }
                )
            }

            composable(
                route = Ruta.Detalle.ruta,
                arguments = listOf(navArgument(Argumentos.PRODUCTO_ID) { type = NavType.LongType })
            ) {
                DetalleProductoScreen(
                    onVolver = { navController.popBackStack() },
                    onRegistrarMovimiento = { id -> navController.navigate(Ruta.Movimiento.crear(id)) },
                    onVerHistorial = { id -> navController.navigate(Ruta.Historial.crear(id)) },
                    mostrarMensaje = mostrarMensaje,
                    puedeEditarFoto = esEncargado
                )
            }

            composable(
                route = Ruta.Movimiento.ruta,
                arguments = listOf(
                    navArgument(Argumentos.PRODUCTO_ID) { type = NavType.LongType; defaultValue = -1L },
                    navArgument(Argumentos.TIPO) { type = NavType.StringType; defaultValue = "" }
                )
            ) {
                MovimientoScreen(
                    perfil = perfil,
                    onVolver = navController.volverSiHayDondeVolver()
                )
            }

            composable(
                route = Ruta.Historial.ruta,
                arguments = listOf(
                    navArgument(Argumentos.PRODUCTO_ID) { type = NavType.LongType; defaultValue = -1L }
                )
            ) {
                HistorialScreen(onVolver = navController.volverSiHayDondeVolver())
            }

            composable(Ruta.Alertas.ruta) {
                AlertasScreen(
                    onVolver = { navController.popBackStack() },
                    onRegistrarIngreso = { id ->
                        navController.navigate(Ruta.Movimiento.crear(id, TipoMovimiento.INGRESO))
                    },
                    mostrarMensaje = mostrarMensaje,
                    puedeIngresar = esEncargado
                )
            }

            composable(
                route = Ruta.NuevoProducto.ruta,
                arguments = listOf(
                    navArgument(Argumentos.CODIGO) { type = NavType.StringType; defaultValue = "" }
                )
            ) {
                NuevoProductoScreen(
                    perfil = perfil,
                    onVolver = { navController.popBackStack() },
                    onGuardado = { id ->
                        mostrarMensaje("Producto guardado")
                        navController.navigate(Ruta.Detalle.crear(id)) {
                            popUpTo(Ruta.Inventario.ruta)  // Al volver desde el detalle se llega al inventario
                        }
                    }
                )
            }

            composable(Ruta.BuscarCodigo.ruta) {
                BuscarCodigoScreen(
                    puedeCrear = esEncargado,
                    onVolver = { navController.popBackStack() },
                    onEncontrado = { id ->
                        navController.navigate(Ruta.Detalle.crear(id)) { popUpTo(Ruta.Inventario.ruta) }
                    },
                    onCrearProducto = { codigo ->
                        navController.navigate(Ruta.NuevoProducto.crear(codigo)) { popUpTo(Ruta.Inventario.ruta) }
                    }
                )
            }
        }
    }
}

/**
 * Navega a una pestaña de la barra inferior sin apilar pantallas repetidas
 * y recordando el estado de cada pestaña.
 */
private fun NavHostController.irAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(Ruta.Inventario.ruta) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Devuelve la acción "volver" solo si la pantalla se abrió desde otra (ej. desde Detalle).
 * Si se abrió desde la barra inferior, devuelve null y no se muestra la flecha.
 */
private fun NavHostController.volverSiHayDondeVolver(): (() -> Unit)? {
    val anterior = previousBackStackEntry?.destination?.route
    return if (anterior != null && anterior != Ruta.Inventario.ruta) {
        { popBackStack() }
    } else null
}
