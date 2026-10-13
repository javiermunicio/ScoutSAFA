package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.ActivityRepository
import es.scoutsafa.app.domain.model.ScoutActivity

class GetActivityByIdUseCase(private val repository: ActivityRepository) {
    suspend operator fun invoke(id: String): Result<ScoutActivity> = repository.getById(id)
}
