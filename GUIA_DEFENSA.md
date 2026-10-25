# Guía para la defensa (EP3)

La defensa es individual, dura 15 minutos y no se puede usar IA. Sigue el orden de la pauta.
Lee cada sección con el archivo abierto en Android Studio.

## 1. Entorno

- Android Studio, Gradle con catálogo de versiones (`gradle/libs.versions.toml`).
- minSdk 30, targetSdk 37. Se ejecuta en emulador o en un teléfono con depuración USB.
- Dependencias extra: Room + KSP, Navigation Compose, DataStore, Coil, Material Icons Extended.

## 2. Arquitectura MVVM

Flujo: **Pantalla → evento → ViewModel → Repository → Room → Flow → ViewModel → StateFlow → Pantalla**.

| Capa | Archivos | Responsabilidad |
|---|---|---|
| Model | `model/`, `data/local/entity/` | Datos y estados inmutables (`data class` con `val`) |
| Repository | `InventarioRepository`, `PreferenciasRepository` | Única puerta a los datos. El ViewModel no sabe si vienen de Room, DataStore o internet |
| ViewModel | `viewmodel/` | Guarda el estado en `MutableStateFlow`, valida y llama al repositorio. Sobrevive a la rotación |
| Screens y Composables | `ui/screens/`, `ui/components/` | Solo dibujan el estado y avisan eventos. Los componentes son stateless (state hoisting) |

**Por qué MVVM:** separa responsabilidades, la lógica se prueba sin pantalla, el estado no se pierde al girar
el teléfono y se puede cambiar la fuente de datos sin tocar la UI.

## 3. Diseño visual

- Material 3 con colores propios (`ui/theme/Color.kt`). Verde, amarillo y rojo para el estado del stock.
- Jerarquía: título grande, datos clave (cantidad) en tamaño grande, datos secundarios en gris.
- Navegación: `NavHost` en `navigation/AppNavigation.kt`, rutas en `navigation/Rutas.kt` (sealed class).
  La barra inferior aparece solo en Inventario, Movimiento e Historial.
- Adaptabilidad: `fillMaxWidth`, `weight`, `verticalScroll` y `LazyColumn` para cualquier tamaño de pantalla.

## 4. Formularios y validación

Formularios: Login, Nuevo producto, Registrar movimiento y Código de barras.

1. El usuario escribe y la pantalla llama a `viewModel.onXChanged(valor)`.
2. El ViewModel copia el estado (`copy`) y borra el error de ese campo.
3. Al guardar, el ViewModel llama a `Validaciones` y guarda los mensajes en `...Errores`.
4. `CampoTexto` muestra borde rojo (`isError`), ícono de error (`trailingIcon`) y mensaje (`supportingText`).

Para mostrar en vivo: en Nuevo producto pulsa "Guardar" con todo vacío; luego escribe un código de 5 dígitos;
luego intenta una salida mayor al stock en Registrar movimiento.

## 5. Estado

- `MutableStateFlow` privado en el ViewModel y `StateFlow` público de solo lectura.
- `combine(...)` junta varias fuentes (ej. productos de Room + búsqueda + filtro en `InventarioViewModel`).
- `stateIn(...)` convierte el Flow en StateFlow para la UI.
- La pantalla usa `collectAsState()` y se recompone sola cuando cambia el estado.
- Estructuras de control: `when` para elegir estado/colores, `if` para mostrar u ocultar según el perfil.

## 6. Animaciones

| Animación | Dónde | Qué aporta |
|---|---|---|
| `enterTransition`/`exitTransition` | `AppNavigation` | Cambio de pantalla fluido |
| `AnimatedVisibility` | Aviso de stock bajo, barra inferior, stock resultante, mensaje de éxito | Aparecer/desaparecer sin saltos |
| `animateColorAsState` | `EstadoChip`, tarjeta de perfil, visor de código | El color cambia suave al cambiar el estado |
| `animateFloatAsState` | Barra de stock en Detalle | Muestra visualmente cuánto falta |
| `animateItem` | Listas | Los elementos se reordenan al filtrar |
| `Crossfade` | `ImagenProducto` | Transición al poner o quitar la foto |
| `CircularProgressIndicator` | `CargaScreen`, botones guardando | Retroalimentación de carga |

## 7. Persistencia

- **Room** (`AppDatabase`): tablas `productos` y `movimientos` con clave foránea.
  - `@Entity` define la tabla, `@Dao` las consultas, `@Database` une todo. Singleton con `@Volatile` y `synchronized`.
  - Las consultas que devuelven `Flow` actualizan la UI sola.
  - `registrarMovimiento` usa `withTransaction`: guarda el movimiento y el nuevo stock juntos, o ninguno.
- **DataStore**: sesión y preferencia de alertas (`PreferenciasRepository`).

Para mostrar: registra una venta, cierra la app por completo y ábrela. El stock y el historial siguen ahí.

## 8. Recursos nativos

- **Cámara:** `TakePicture()` + permiso `CAMERA` pedido con `RequestPermission()`. La foto se guarda en
  `files/fotos/` y se comparte con la cámara mediante `FileProvider` (`res/xml/file_paths.xml`, `AndroidManifest.xml`).
- **Galería:** `PickVisualMedia()`. No pide permiso. La imagen se copia a la carpeta de la app para no perder el acceso.
- **Notificaciones:** canal creado en `MainActivity`, permiso `POST_NOTIFICATIONS` pedido en Alertas, aviso al quedar bajo el mínimo.
- Presentación: `ImagenProducto` recorta la foto (`ContentScale.Crop`) con bordes redondeados (`clip`) y borde.
- Si se cancela la cámara o la galería, la app no hace nada y no se cae.

## 9. Cambios en vivo que te pueden pedir

**Agregar una validación** (ej. la ubicación debe tener al menos 5 caracteres):
1. En `Validaciones.ubicacion` agrega `valor.trim().length < 5 -> "Mínimo 5 caracteres"` dentro de un `when`.
2. No hay que tocar la pantalla: el error ya se muestra.

**Agregar un campo a un formulario** (ej. "Proveedor" en Nuevo producto):
1. `NuevoProductoUiState`: agrega `val proveedor: String = ""`; en `NuevoProductoErrores` agrega `val proveedor: String? = null` y súmalo a la lista de `hayErrores`.
2. `NuevoProductoViewModel`: crea `onProveedorChanged` (copia el patrón de `onUbicacionChanged`) y valida en `validar()`.
3. `ProductoEntity`: agrega `val proveedor: String = ""` y sube `version` en `AppDatabase` (la base se recrea).
4. En `guardar()` pasa `proveedor = s.proveedor.trim()`.
5. `NuevoProductoScreen`: agrega un `CampoTexto` igual a los otros.

**Crear un componente nuevo** (ej. tarjeta con el total de unidades): crea `ui/components/TarjetaResumen.kt` con un
`@Composable` que reciba los datos por parámetro, y llámalo desde `InventarioScreen` usando datos del `state`.

**Agregar una animación:** envuelve cualquier elemento con `AnimatedVisibility(visible = condición) { ... }`
o cambia un color fijo por `animateColorAsState(...)`.

**Cambiar la persistencia** (ej. eliminar un producto): en `ProductoDao` agrega `@Delete suspend fun eliminar(p: ProductoEntity)`,
en el repositorio una función que lo llame, en el ViewModel una función con `viewModelScope.launch`, y un botón en la pantalla.
