package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.AnnouncementRepository
import es.scoutsafa.app.domain.model.Announcement
import kotlinx.coroutines.flow.Flow

class ListAnnouncementsUseCase(private val repository: AnnouncementRepository) {
    operator fun invoke(): Flow<List<Announcement>> = repository.listAll()
}
