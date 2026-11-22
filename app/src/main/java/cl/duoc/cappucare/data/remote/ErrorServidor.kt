package cl.duoc.cappucare.data.remote

/**
 * Error al hablar con un microservicio. El mensaje ya viene listo para mostrarlo en pantalla,
 * por ejemplo "No hay stock suficiente para esta salida" o "No se pudo conectar con el servidor".
 *
 * @property codigo código HTTP de la respuesta (409, 400, 404...) o null si no hubo conexión.
 */
class ErrorServidor(mensaje: String, val codigo: Int? = null) : Exception(mensaje)
