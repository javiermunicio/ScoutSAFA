package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.AttendanceRepository
import es.scoutsafa.app.domain.model.ActivityAttendance
import kotlinx.coroutines.flow.Flow

class ObserveAttendanceUseCase(private val repository: AttendanceRepository) {
    operator fun invoke(activityId: String): Flow<List<ActivityAttendance>> =
        repository.observeByActivity(activityId)
}
