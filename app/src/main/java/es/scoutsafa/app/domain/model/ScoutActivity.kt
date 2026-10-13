package es.scoutsafa.app.domain.model

/**
 * Actividad del grupo (reunión, salida, acampada...). Se llama ScoutActivity
 * para no chocar con android.app.Activity.
 */
data class ScoutActivity(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    /** Valor de [Section]; "" significa que es para todo el grupo. */
    val section: String = "",
    val location: String = "",
    val meetingPoint: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val createdBy: String = "",
    val createdAt: Long = 0
)
