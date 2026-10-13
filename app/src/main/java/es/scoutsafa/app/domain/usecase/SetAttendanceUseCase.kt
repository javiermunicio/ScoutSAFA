package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.AttendanceRepository
import es.scoutsafa.app.domain.model.ActivityAttendance
import es.scoutsafa.app.domain.model.AttendanceStatus
import es.scoutsafa.app.domain.model.User

class SetAttendanceUseCase(private val repository: AttendanceRepository) {
    suspend operator fun invoke(
        activityId: String,
        user: User,
        status: AttendanceStatus
    ): Boolean = repository.set(
        ActivityAttendance(
            activityId = activityId,
            userId = user.id,
            userName = user.fullName,
            status = status.name,
            updatedAt = System.currentTimeMillis()
        )
    )
}
