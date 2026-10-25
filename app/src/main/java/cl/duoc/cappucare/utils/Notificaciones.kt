package cl.duoc.cappucare.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.cappucare.R
import cl.duoc.cappucare.data.local.entity.ProductoEntity

/**
 * Notificaciones del sistema para avisar cuando un producto queda bajo su nivel mínimo.
 */
object Notificaciones {

    private const val CANAL_STOCK = "stock_bajo"

    /** Crea el canal de notificaciones (obligatorio desde Android 8). Se llama al iniciar la app. */
    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_STOCK,
            "Stock bajo",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos cuando un producto llega a su nivel mínimo"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    /** true si la app puede mostrar notificaciones (en Android 13+ el usuario debe aceptarlo). */
    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    /** Muestra el aviso de stock bajo. Si no hay permiso, no hace nada. */
    fun avisarStockBajo(context: Context, producto: ProductoEntity) {
        if (!tienePermiso(context)) return

        val texto = if (producto.cantidad == 0) {
            "Agotado. Mínimo: ${producto.stockMinimo} ${producto.unidad.abreviatura}"
        } else {
            "Quedan ${producto.cantidad} ${producto.unidad.abreviatura} (mínimo ${producto.stockMinimo})"
        }

        val notificacion = NotificationCompat.Builder(context, CANAL_STOCK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${producto.nombre} necesita reposición")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(producto.id.toInt(), notificacion)
        } catch (e: SecurityException) {
            // El usuario quitó el permiso justo antes: no se muestra el aviso
        }
    }
}
