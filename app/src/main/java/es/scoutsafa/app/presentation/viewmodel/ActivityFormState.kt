package es.scoutsafa.app.presentation.viewmodel

import es.scoutsafa.app.domain.model.ScoutActivity
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.util.DateUtils
import es.scoutsafa.app.util.DateValidator

/** Estado del formulario de actividad, compartido por las pantallas de crear y editar. */
data class ActivityFormState(
    val title: String = "",
    val description: String = "",
    val section: Section? = null,
    val location: String = "",
    val meetingPoint: String = "",
    val startDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = ""
) {
    /** Devuelve un mensaje de error, o null si el formulario es válido. */
    fun validate(): String? {
        if (title.isBlank()) return "El título es obligatorio"
        DateValidator.validate(startDate)?.let { return "Inicio: $it" }
        DateValidator.validateTime(startTime)?.let { return "Inicio: $it" }
        DateValidator.validate(endDate)?.let { return "Fin: $it" }
        DateValidator.validateTime(endTime)?.let { return "Fin: $it" }
        val start = DateUtils.parse(startDate, startTime) ?: return "La fecha de inicio no es válida"
        val end = DateUtils.parse(endDate, endTime) ?: return "La fecha de fin no es válida"
        if (end <= start) return "El fin debe ser posterior al inicio"
        return null
    }

    /** Aplica el formulario sobre [base] (al editar conserva id, createdBy y createdAt). */
    fun toActivity(base: ScoutActivity = ScoutActivity()): ScoutActivity = base.copy(
        title = title.trim(),
        description = description.trim(),
        section = section?.name ?: "",
        location = location.trim(),
        meetingPoint = meetingPoint.trim(),
        startTime = DateUtils.parse(startDate, startTime) ?: 0,
        endTime = DateUtils.parse(endDate, endTime) ?: 0
    )

    companion object {
        fun from(activity: ScoutActivity) = ActivityFormState(
            title = activity.title,
            description = activity.description,
            section = Section.fromName(activity.section),
            location = activity.location,
            meetingPoint = activity.meetingPoint,
            startDate = DateUtils.formatDate(activity.startTime),
            startTime = DateUtils.formatTime(activity.startTime),
            endDate = DateUtils.formatDate(activity.endTime),
            endTime = DateUtils.formatTime(activity.endTime)
        )
    }
}
