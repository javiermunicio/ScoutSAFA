package es.scoutsafa.app.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Conversión entre milisegundos y los textos que se muestran/editan en pantalla.
 * Usa SimpleDateFormat (y no java.time) porque minSdk es 24 y java.time requiere API 26.
 */
object DateUtils {

    private val zone: TimeZone = TimeZone.getTimeZone("Europe/Madrid")
    private val locale = Locale("es", "ES")

    private fun formatter(pattern: String) = SimpleDateFormat(pattern, locale).apply {
        timeZone = zone
        isLenient = false
    }

    fun formatDateTime(millis: Long): String = formatter("dd/MM/yyyy HH:mm").format(millis)
    fun formatDate(millis: Long): String = formatter("dd/MM/yyyy").format(millis)
    fun formatTime(millis: Long): String = formatter("HH:mm").format(millis)

    /** Convierte "DD/MM/AAAA" + "HH:MM" a milisegundos, o null si no se puede interpretar. */
    fun parse(date: String, time: String): Long? = try {
        formatter("dd/MM/yyyy HH:mm").parse("$date $time")?.time
    } catch (e: Exception) {
        null
    }
}
