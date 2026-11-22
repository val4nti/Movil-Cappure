package cl.duoc.cappucare.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API externa mindicador.cl (GET https://mindicador.cl/api).
 * El JSON trae muchos indicadores; Gson solo llena los que declaramos aquí y el resto se ignora.
 *
 * Ejemplo (resumido):
 * {"uf":{"codigo":"uf","nombre":"Unidad de fomento (UF)","unidad_medida":"Pesos",
 *        "fecha":"2026-11-22T03:00:00.000Z","valor":39876.54},
 *  "dolar":{...,"valor":948.12}}
 */
data class IndicadoresDto(
    val uf: IndicadorDto?,
    val dolar: IndicadorDto?
)

data class IndicadorDto(
    val codigo: String?,
    val nombre: String?,
    // @SerializedName: el JSON usa "unidad_medida" y en Kotlin lo llamamos unidadMedida
    @SerializedName("unidad_medida") val unidadMedida: String?,
    val fecha: String?,
    val valor: Double?
)
