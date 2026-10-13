package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.domain.model.User
import es.scoutsafa.app.domain.model.sectionOrNull
import es.scoutsafa.app.domain.usecase.SaveProfileUseCase
import es.scoutsafa.app.notifications.FcmTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    userRepository: UserRepository,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    val currentUser: StateFlow<User?> = currentUserFlow(authRepository, userRepository)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val phone = MutableStateFlow("")
    val section = MutableStateFlow<Section?>(null)

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut

    init {
        // Rellena los campos editables con los datos guardados, una sola vez.
        viewModelScope.launch {
            val user = currentUser.filterNotNull().first()
            phone.value = user.phone
            section.value = user.sectionOrNull()
        }
    }

    fun save() {
        val uid = authRepository.currentUser?.uid ?: return
        val phoneValue = phone.value.trim()
        if (phoneValue.isNotEmpty() && !Regex("""^\+?[0-9 ]{9,15}$""").matches(phoneValue)) {
            _message.value = "El teléfono no es válido"
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            val ok = saveProfileUseCase(uid, phoneValue, section.value?.name ?: "")
            if (ok) fcmTokenManager.updateTopics(section.value?.name)
            _message.value = if (ok) "Perfil guardado" else "No se pudo guardar el perfil"
            _isSaving.value = false
        }
    }

    fun clearMessage() { _message.value = null }

    fun logout() {
        authRepository.logout()
        _loggedOut.value = true
    }
}
