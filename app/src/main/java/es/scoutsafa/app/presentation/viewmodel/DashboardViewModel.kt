package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.Announcement
import es.scoutsafa.app.domain.model.ScoutActivity
import es.scoutsafa.app.domain.model.User
import es.scoutsafa.app.domain.model.sectionVisibleFor
import es.scoutsafa.app.domain.usecase.ListActivitiesUseCase
import es.scoutsafa.app.domain.usecase.ListAnnouncementsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    listActivitiesUseCase: ListActivitiesUseCase,
    listAnnouncementsUseCase: ListAnnouncementsUseCase,
    userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = currentUserFlow(authRepository, userRepository)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val allActivities = listActivitiesUseCase()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allAnnouncements = listAnnouncementsUseCase()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Las próximas 3 actividades (que no hayan terminado) de su rama o de todo el grupo. */
    val upcomingActivities: StateFlow<List<ScoutActivity>> =
        combine(allActivities, currentUser) { activities, user ->
            val now = System.currentTimeMillis()
            val userSection = user?.section.orEmpty()
            activities
                .filter { it.endTime >= now && sectionVisibleFor(it.section, userSection) }
                .take(3)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Anuncios fijados que le afectan. */
    val pinnedAnnouncements: StateFlow<List<Announcement>> =
        combine(allAnnouncements, currentUser) { announcements, user ->
            val userSection = user?.section.orEmpty()
            announcements
                .filter { it.pinned && sectionVisibleFor(it.section, userSection) }
                .take(3)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
