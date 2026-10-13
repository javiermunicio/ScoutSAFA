package es.scoutsafa.app.domain.usecase

import es.scoutsafa.app.data.repositories.UserRepository

class SaveProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userId: String, phone: String, section: String): Boolean =
        repository.updateProfile(userId, phone.trim(), section)
}
