# AppCappucare

App Android para el control de existencias de productos e insumos médicos de **Cappucare**
(caso semestral DSY1105 Desarrollo de Aplicaciones Móviles). Prototipo académico con datos ficticios.

**Integrantes:** Valentina Morales

## Stack

- Kotlin + Jetpack Compose (Material 3)
- Arquitectura MVVM con StateFlow
- Navigation Compose
- Room (SQLite) para productos y movimientos
- DataStore Preferences para la sesión y la configuración de alertas
- Coil para mostrar las fotos de productos

## Funcionalidades

- **Inicio de sesión** con usuario, contraseña y perfil (Encargado de bodega o Vendedor). La sesión queda guardada.
- **Inventario:** listado con búsqueda por nombre o código, filtro de stock bajo y aviso de productos bajo el mínimo.
- **Detalle de producto:** stock con barra respecto del mínimo, datos del producto, último movimiento y foto.
- **Registrar movimiento:** ingresos y salidas (venta, merma, ajuste) con stock resultante en vivo.
- **Historial** de movimientos con filtro por tipo, general o por producto.
- **Alertas de stock:** productos por reponer y opción de recibir notificaciones.
- **Nuevo producto:** formulario con validación por campo.
- **Búsqueda por código de barras** escrito a mano con teclado numérico.
- **Reglas por perfil:** el Vendedor no crea productos, no cambia fotos y solo registra salidas.

### Formularios y validaciones

Todas las reglas están en `utils/Validaciones.kt` y se aplican desde los ViewModels. Cada campo con error
muestra borde rojo, ícono de error y mensaje debajo.

### Recursos nativos

| Recurso | Dónde | Permiso |
|---|---|---|
| Cámara | Detalle de producto → "Cámara" | `CAMERA`, pedido en tiempo de ejecución. La foto se guarda con `FileProvider` |
| Galería | Detalle de producto → "Galería" | No necesita permiso (selector de fotos de Android) |
| Notificaciones | Al quedar un producto bajo el mínimo | `POST_NOTIFICATIONS` (Android 13+), pedido en Alertas |

### Animaciones

Transiciones entre pantallas, barra inferior que aparece y desaparece, color animado de los estados de stock,
aviso de stock bajo con `AnimatedVisibility`, lista con `animateItem`, barra de stock con `animateFloatAsState`,
mensaje de éxito animado, cambio de foto con `Crossfade` y pantalla de carga inicial.

## Estructura

```
cl.duoc.cappucare
├── data
│   ├── local
│   │   ├── entity/        ProductoEntity, MovimientoEntity, MovimientoConProducto
│   │   ├── dao/           ProductoDao, MovimientoDao
│   │   ├── AppDatabase    Base de datos Room (Singleton)
│   │   └── DatosIniciales Catálogo e historial ficticios del primer uso
│   ├── preferences/       PreferenciasRepository (DataStore)
│   └── repository/        InventarioRepository
├── model/                 Estados de UI, errores de formularios y enums
├── navigation/            Rutas y AppNavigation (NavHost + barra inferior)
├── utils/                 Validaciones, GestorImagenes, Notificaciones, Formato
├── viewmodel/             Un ViewModel por pantalla + SesionViewModel compartido
├── ui
│   ├── theme/             Colores y tema Material 3
│   ├── components/        Componentes reutilizables (CampoTexto, selectores, ImagenProducto…)
│   └── screens/           Pantallas, una carpeta por pantalla
└── MainActivity
```

## Cómo ejecutar

1. Abrir la carpeta en Android Studio.
2. Esperar la sincronización de Gradle.
3. Ejecutar en un emulador o dispositivo con Android 11 (API 30) o superior.
4. Ingresar con cualquier usuario (3+ caracteres, sin espacios) y contraseña (4+ caracteres), y elegir un perfil.

La primera vez se cargan 7 productos y 7 movimientos de ejemplo.
