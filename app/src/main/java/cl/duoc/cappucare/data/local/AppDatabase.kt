package cl.duoc.cappucare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.cappucare.data.local.dao.MovimientoDao
import cl.duoc.cappucare.data.local.dao.ProductoDao
import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity

/**
 * Base de datos Room de la aplicación, con las tablas de productos y movimientos.
 *
 * Se usa el patrón Singleton: existe una sola instancia de la base de datos para toda la app,
 * porque crearla es costoso y tener varias abiertas podría generar datos inconsistentes.
 */
@Database(
    entities = [ProductoEntity::class, MovimientoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val productoDao: ProductoDao
    abstract val movimientoDao: MovimientoDao

    companion object {
        // @Volatile hace que todos los hilos vean siempre el valor más reciente
        @Volatile
        private var instancia: AppDatabase? = null

        /** Devuelve la instancia única, creándola la primera vez que se pide. */
        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = "cappucare_database"
                )
                    // Prototipo académico: si cambia la estructura de las tablas, se recrea la base
                    // (se pierden los datos y se vuelve a cargar el catálogo de ejemplo)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
