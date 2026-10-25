package cl.duoc.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.cappucare.data.preferences.PreferenciasRepository
import cl.duoc.cappucare.model.EstadoSesion
import cl.duoc.cappucare.model.LoginUiState
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.Sesion
import cl.duoc.cappucare.utils.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel compartido de la sesión. Se crea en MainActivity y lo usan el login
 * y la navegación, para saber quién está conectado y con qué perfil.
 */
class SesionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferencias = PreferenciasRepository(application)

    /**
     * Sesión guardada en DataStore. Se agrega una pequeña espera inicial para mostrar
     * la pantalla de carga (simulación de carga de datos, como en la guía 12).
     */
    val estadoSesion: StateFlow<EstadoSesion> = flow {
        delay(800)
        emitAll(preferencias.sesion.map { sesion ->
            if (sesion == null) EstadoSesion.Inactiva else EstadoSesion.Activa(sesion)
        })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EstadoSesion.Cargando
    )

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    fun onUsuarioChanged(valor: String) = _login.update {
        it.copy(usuario = valor, errores = it.errores.copy(usuario = null))
    }

    fun onContrasenaChanged(valor: String) = _login.update {
        it.copy(contrasena = valor, errores = it.errores.copy(contrasena = null))
    }

    fun onPerfilChanged(perfil: Perfil) = _login.update {
        it.copy(perfil = perfil, errores = it.errores.copy(perfil = null))
    }

    fun onMostrarContrasena() = _login.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }

    /**
     * Valida el formulario. Si todo está bien guarda la sesión y devuelve true.
     * Si algo falla, deja los mensajes de error en el estado y devuelve false.
     */
    fun ingresar(): Boolean {
        val actual = _login.value
        val errores = actual.errores.copy(
            usuario = Validaciones.usuario(actual.usuario),
            contrasena = Validaciones.contrasena(actual.contrasena),
            perfil = if (actual.perfil == null) "Selecciona un perfil" else null
        )
        _login.update { it.copy(errores = errores) }

        val valido = errores.usuario == null && errores.contrasena == null && errores.perfil == null
        if (valido) {
            val sesion = Sesion(usuario = actual.usuario.trim(), perfil = actual.perfil!!)
            viewModelScope.launch {
                preferencias.iniciarSesion(sesion)
                _login.value = LoginUiState()   // Limpia el formulario para el próximo ingreso
            }
        }
        return valido
    }

    fun cerrarSesion() {
        viewModelScope.launch { preferencias.cerrarSesion() }
    }
}
