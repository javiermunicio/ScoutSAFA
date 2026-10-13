package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.ActivityRepository
import es.scoutsafa.app.domain.model.ScoutActivity
import kotlinx.coroutines.flow.Flow

class ListActivitiesUseCase(private val repository: ActivityRepository) {
    operator fun invoke(): Flow<List<ScoutActivity>> = repository.listAll()
}
