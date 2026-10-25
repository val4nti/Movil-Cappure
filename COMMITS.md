# Plan de commits

La rúbrica revisa commits distribuidos entre los dos integrantes y con mensajes técnicos.
Repártanse los commits (ej. A los impares y B los pares) y suban cada uno los suyos.
Creen una tarjeta de Trello por cada fila y muévanla a "Finalizado" al subir el commit.

| # | Archivos | Mensaje |
|---|---|---|
| 1 | Proyecto base: `build.gradle.kts`, `settings.gradle.kts`, `gradle/`, `app/build.gradle.kts`, `res/`, `AndroidManifest.xml`, `ui/theme/` | `chore: configura proyecto Compose con Room, Navigation, DataStore y Coil` |
| 2 | `model/UnidadMedida`, `Categorias`, `EstadoStock`, `TipoMovimiento`, `data/local/entity/` | `feat(data): agrega entidades Producto y Movimiento con enums de dominio` |
| 3 | `data/local/dao/`, `AppDatabase`, `DatosIniciales` | `feat(data): implementa DAOs y base de datos Room con datos de ejemplo` |
| 4 | `data/repository/InventarioRepository` | `feat(data): agrega repositorio con registro de movimientos transaccional` |
| 5 | `model/InventarioUiState`, `viewmodel/InventarioViewModel`, `ui/components/EstadoChip`, `ProductoItem`, `AvisoStockBajo`, `ui/screens/inventario/` | `feat(inventario): lista de productos con búsqueda, filtros y aviso de stock bajo` |
| 6 | `navigation/`, `MainActivity` (versión con navegación) | `feat(navegacion): agrega NavHost, rutas y barra inferior` |
| 7 | `utils/Validaciones`, `ui/components/CampoTexto`, `SelectorOpciones`, `SelectorDesplegable`, `BarraSuperior` | `feat(ui): componentes de formulario con validación visual` |
| 8 | `model/Sesion`, `LoginUiState`, `data/preferences/`, `viewmodel/SesionViewModel`, `ui/screens/login/`, `ui/screens/carga/` | `feat(login): inicio de sesión con perfil persistido en DataStore` |
| 9 | `model/NuevoProductoUiState`, `viewmodel/NuevoProductoViewModel`, `ui/screens/producto/` | `feat(productos): formulario de nuevo producto con validaciones` |
| 10 | `model/MovimientoUiState`, `viewmodel/MovimientoViewModel`, `ui/components/MensajeExito`, `ui/screens/movimiento/` | `feat(movimientos): registro de ingresos y salidas con stock resultante` |
| 11 | `model/HistorialUiState`, `viewmodel/HistorialViewModel`, `ui/screens/historial/`, `utils/Formato` | `feat(historial): historial de movimientos con filtro por tipo` |
| 12 | `model/DetalleUiState`, `viewmodel/DetalleProductoViewModel`, `ui/components/ImagenProducto`, `ui/screens/detalle/`, `utils/GestorImagenes`, `res/xml/file_paths.xml` | `feat(nativo): detalle de producto con foto desde cámara y galería` |
| 13 | `utils/Notificaciones`, `viewmodel/AlertasViewModel`, `ui/screens/alertas/` | `feat(nativo): alertas de stock bajo con notificaciones` |
| 14 | `viewmodel/BuscarCodigoViewModel`, `ui/screens/codigo/` | `feat(productos): búsqueda por código de barras con teclado numérico` |
| 15 | `README.md` | `docs: agrega README con funcionalidades y pasos de ejecución` |

Si la app compila solo con todo junto, igual pueden subir por partes: agreguen los archivos de cada fila
con `git add <archivos>` y hagan el commit, aunque los commits intermedios no compilen.
Recuerden: el repositorio debe ser **público**, con acceso al docente, y **no se puede tocar después de la fecha de entrega**.
