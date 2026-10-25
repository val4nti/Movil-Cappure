package cl.duoc.cappucare.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Maneja los archivos de las fotos de productos.
 *
 * Las fotos se guardan en la carpeta privada de la app (files/fotos/), así siguen disponibles
 * aunque se cierre la app. El FileProvider (declarado en el AndroidManifest) convierte el
 * archivo en una Uri "content://" segura que se puede compartir con la app de cámara.
 */
object GestorImagenes {

    private fun carpetaFotos(context: Context): File =
        File(context.filesDir, "fotos").apply { mkdirs() }

    private fun uriDe(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    /** Crea un archivo vacío y devuelve su Uri, para que la cámara escriba la foto ahí. */
    fun crearUriParaFoto(context: Context): Uri {
        val archivo = File(carpetaFotos(context), "producto_${System.currentTimeMillis()}.jpg")
        return uriDe(context, archivo)
    }

    /**
     * Copia una imagen elegida en la galería a la carpeta de la app.
     * Se copia porque el permiso para leer la imagen original se pierde al cerrar la app.
     *
     * @return la Uri de la copia, o null si no se pudo leer la imagen.
     */
    fun copiarDesdeGaleria(context: Context, origen: Uri): Uri? {
        val destino = File(carpetaFotos(context), "producto_${System.currentTimeMillis()}.jpg")
        val entrada = context.contentResolver.openInputStream(origen) ?: return null
        entrada.use { input ->
            destino.outputStream().use { output -> input.copyTo(output) }
        }
        return uriDe(context, destino)
    }
}
