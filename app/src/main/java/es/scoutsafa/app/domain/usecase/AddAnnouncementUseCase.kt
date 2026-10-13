package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.AnnouncementRepository
import es.scoutsafa.app.domain.model.Announcement

class AddAnnouncementUseCase(private val repository: AnnouncementRepository) {
    suspend operator fun invoke(announcement: Announcement): Boolean =
        repository.save(announcement.copy(createdAt = System.currentTimeMillis()))
}
