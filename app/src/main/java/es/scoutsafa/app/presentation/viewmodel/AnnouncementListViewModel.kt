package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.Announcement
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.domain.model.isScouter
import es.scoutsafa.app.domain.usecase.DeleteAnnouncementUseCase
import es.scoutsafa.app.domain.usecase.ListAnnouncementsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementListViewModel(
    listAnnouncementsUseCase: ListAnnouncementsUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {

    /** null = todas las ramas */
    val selectedSection = MutableStateFlow<Section?>(null)

    private val allAnnouncements = listAnnouncementsUseCase()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isScouter: StateFlow<Boolean> = currentUserFlow(authRepository, userRepository)
        .map { it?.isScouter() == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /** Los fijados primero; dentro de cada grupo, el más reciente arriba. */
    val announcements: StateFlow<List<Announcement>> = combine(
        allAnnouncements, selectedSection
    ) { announcements, section ->
        announcements
            .filter { section == null || it.section.isBlank() || it.section == section.name }
            .sortedWith(compareByDescending<Announcement> { it.pinned }.thenByDescending { it.createdAt })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(id: String) {
        viewModelScope.launch { deleteAnnouncementUseCase(id) }
    }
}
