package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.ActivityRepository

class DeleteActivityUseCase(private val repository: ActivityRepository) {
    suspend operator fun invoke(id: String): Boolean = repository.delete(id)
}
