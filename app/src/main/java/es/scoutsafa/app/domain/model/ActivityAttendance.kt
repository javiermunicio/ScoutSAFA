package es.scoutsafa.app.domain.model

enum class AttendanceStatus { CONFIRMED, DECLINED }

data class ActivityAttendance(
    val id: String = "",
    val activityId: String = "",
    val userId: String = "",
    val userName: String = "",
    /** Valor de [AttendanceStatus]. */
    val status: String = AttendanceStatus.CONFIRMED.name,
    val updatedAt: Long = 0
)
