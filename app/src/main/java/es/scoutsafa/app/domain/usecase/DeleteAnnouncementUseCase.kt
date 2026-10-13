package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.AnnouncementRepository

class DeleteAnnouncementUseCase(private val repository: AnnouncementRepository) {
    suspend operator fun invoke(id: String): Boolean = repository.delete(id)
}
