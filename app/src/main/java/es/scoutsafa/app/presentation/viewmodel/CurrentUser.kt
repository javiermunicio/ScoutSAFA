package es.scoutsafa.app.presentation.viewmodel

import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf

/** Perfil del usuario autenticado, actualizado en tiempo real (null si no hay sesión o falla la lectura). */
internal fun currentUserFlow(
    authRepository: AuthRepository,
    userRepository: UserRepository
): Flow<User?> {
    val uid = authRepository.currentUser?.uid ?: return flowOf(null)
    return userRepository.observeById(uid).catch { emit(null) }
}
