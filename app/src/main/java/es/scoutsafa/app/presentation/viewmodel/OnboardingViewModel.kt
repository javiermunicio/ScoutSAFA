package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.domain.usecase.SaveProfileUseCase
import es.scoutsafa.app.notifications.FcmTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val authRepository: AuthRepository,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _section = MutableStateFlow<Section?>(null)
    val section: StateFlow<Section?> = _section

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onPhoneChanged(value: String) { _phone.value = value }
    fun onSectionSelected(value: Section?) { _section.value = value }

    fun save() {
        val uid = authRepository.currentUser?.uid ?: return
        val phone = _phone.value.trim()
        if (phone.isNotEmpty() && !Regex("""^\+?[0-9 ]{9,15}$""").matches(phone)) {
            _errorMessage.value = "El teléfono no es válido"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val ok = saveProfileUseCase(uid, phone, _section.value?.name ?: "")
            if (ok) {
                fcmTokenManager.updateTopics(_section.value?.name)
                _saved.value = true
                _errorMessage.value = null
            } else {
                _errorMessage.value = "No se pudo guardar. Inténtalo de nuevo."
            }
            _isLoading.value = false
        }
    }

    fun clearError() { _errorMessage.value = null }
}
