package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.ActivityRepository
import es.scoutsafa.app.domain.model.ScoutActivity

class UpdateActivityUseCase(private val repository: ActivityRepository) {
    suspend operator fun invoke(activity: ScoutActivity): Boolean = repository.update(activity)
}
