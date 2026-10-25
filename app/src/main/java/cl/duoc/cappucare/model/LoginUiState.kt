package cl.duoc.cappucare.model

/** Mensajes de error del formulario de inicio de sesión (null = sin error). */
data class LoginErrores(
    val usuario: String? = null,
    val contrasena: String? = null,
    val perfil: String? = null
)

/** Estado del formulario de inicio de sesión. */
data class LoginUiState(
    val usuario: String = "",
    val contrasena: String = "",
    val perfil: Perfil? = null,
    val mostrarContrasena: Boolean = false,
    val errores: LoginErrores = LoginErrores()
)

/** Estado de la sesión al abrir la app. */
sealed interface EstadoSesion {
    /** Todavía se está leyendo DataStore: se muestra la pantalla de carga. */
    data object Cargando : EstadoSesion
    data object Inactiva : EstadoSesion
    data class Activa(val sesion: Sesion) : EstadoSesion
}
