package cl.duoc.cappucare.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cl.duoc.cappucare.model.Perfil
import cl.duoc.cappucare.model.Sesion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Archivo de DataStore de la app. El delegado garantiza una sola instancia.
private val Context.dataStore by preferencesDataStore(name = "preferencias_cappucare")

/**
 * Guarda datos pequeños clave-valor con DataStore Preferences:
 * - La sesión iniciada (usuario y perfil), para no pedir login cada vez que se abre la app.
 * - Si el usuario quiere recibir notificaciones de stock bajo.
 *
 * Room se usa para los datos del negocio (productos, movimientos);
 * DataStore se usa para preferencias y configuración.
 */
class PreferenciasRepository(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    private companion object {
        val USUARIO = stringPreferencesKey("usuario")
        val PERFIL = stringPreferencesKey("perfil")
        val AVISAR_MINIMO = booleanPreferencesKey("avisar_minimo")
    }

    /** Sesión guardada, o null si nadie ha iniciado sesión. */
    val sesion: Flow<Sesion?> = dataStore.data.map { preferencias ->
        val usuario = preferencias[USUARIO]
        val perfil = preferencias[PERFIL]?.let { nombre ->
            Perfil.entries.firstOrNull { it.name == nombre }
        }
        if (usuario != null && perfil != null) Sesion(usuario, perfil) else null
    }

    /** true si hay que avisar con una notificación cuando un producto llega al mínimo. */
    val avisarMinimo: Flow<Boolean> = dataStore.data.map { it[AVISAR_MINIMO] ?: true }

    suspend fun iniciarSesion(sesion: Sesion) {
        dataStore.edit {
            it[USUARIO] = sesion.usuario
            it[PERFIL] = sesion.perfil.name
        }
    }

    suspend fun cerrarSesion() {
        dataStore.edit {
            it.remove(USUARIO)
            it.remove(PERFIL)
        }
    }

    suspend fun cambiarAvisarMinimo(activo: Boolean) {
        dataStore.edit { it[AVISAR_MINIMO] = activo }
    }
}
