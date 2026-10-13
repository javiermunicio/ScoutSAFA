package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.Announcement
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.domain.model.User
import es.scoutsafa.app.domain.model.isScouter
import es.scoutsafa.app.domain.usecase.AddAnnouncementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddAnnouncementViewModel(
    private val addAnnouncementUseCase: AddAnnouncementUseCase,
    userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val currentUser: StateFlow<User?> = currentUserFlow(authRepository, userRepository)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val title = MutableStateFlow("")
    val body = MutableStateFlow("")
    /** null = todo el grupo */
    val section = MutableStateFlow<Section?>(null)
    val pinned = MutableStateFlow(false)

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

    fun save() {
        viewModelScope.launch {
            val user = currentUser.value
            if (user == null || !user.isScouter()) {
                _errorMessage.value = "Solo los scouters pueden publicar anuncios"
                return@launch
            }
            if (title.value.isBlank() || body.value.isBlank()) {
                _errorMessage.value = "El título y el texto son obligatorios"
                return@launch
            }

            _isSaving.value = true
            val ok = addAnnouncementUseCase(
                Announcement(
                    title = title.value.trim(),
                    body = body.value.trim(),
                    section = section.value?.name ?: "",
                    authorId = user.id,
                    authorName = user.fullName,
                    pinned = pinned.value
                )
            )
            if (ok) _saved.value = true else _errorMessage.value = "No se pudo publicar el anuncio"
            _isSaving.value = false
        }
    }
}
