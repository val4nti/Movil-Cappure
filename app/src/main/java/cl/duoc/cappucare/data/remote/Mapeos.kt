package cl.duoc.cappucare.data.remote

import cl.duoc.cappucare.data.local.entity.MovimientoEntity
import cl.duoc.cappucare.data.local.entity.ProductoEntity
import cl.duoc.cappucare.data.remote.dto.MovimientoDto
import cl.duoc.cappucare.data.remote.dto.ProductoDto
import cl.duoc.cappucare.model.TipoMovimiento
import cl.duoc.cappucare.model.UnidadMedida
import java.time.LocalDateTime
import java.time.ZoneId

/*
 * Conversiones entre lo que viaja por la red (DTO) y lo que se guarda en Room (Entity).
 * Son funciones de extensión: se usan como producto.aDto() o dto.aEntity().
 */

fun ProductoEntity.aDto(): ProductoDto = ProductoDto(
    id = if (id == 0L) null else id,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    categoria = categoria,
    cantidad = cantidad,
    unidad = unidad.name,               // enum → texto, ej. UnidadMedida.CAJA → "CAJA"
    stockMinimo = stockMinimo,
    ubicacion = ubicacion,
    activo = activo
)

/** @param fotoUri la foto se guarda solo en el teléfono, así que se conserva la que ya había. */
fun ProductoDto.aEntity(fotoUri: String? = null): ProductoEntity = ProductoEntity(
    id = id ?: 0,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion.orEmpty(),
    categoria = categoria,
    cantidad = cantidad,
    unidad = UnidadMedida.valueOf(unidad),   // texto → enum
    stockMinimo = stockMinimo,
    ubicacion = ubicacion.orEmpty(),
    activo = activo ?: true,
    fotoUri = fotoUri
)

fun MovimientoEntity.aDto(): MovimientoDto = MovimientoDto(
    productoId = productoId,
    tipo = tipo.name,
    cantidad = cantidad,
    motivo = motivo,
    perfil = perfil
)

fun MovimientoDto.aEntity(): MovimientoEntity = MovimientoEntity(
    id = id ?: 0,
    productoId = productoId,
    tipo = TipoMovimiento.valueOf(tipo),
    cantidad = cantidad,
    motivo = motivo,
    perfil = perfil,
    fecha = fecha?.let { textoAMilisegundos(it) } ?: System.currentTimeMillis()
)

/** "2026-11-22T18:13:12.320495" (hora de Chile del servidor) → milisegundos, como guarda Room. */
fun textoAMilisegundos(fecha: String): Long =
    LocalDateTime.parse(fecha).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
