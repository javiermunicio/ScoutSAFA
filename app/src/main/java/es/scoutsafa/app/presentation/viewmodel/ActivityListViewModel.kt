package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.ScoutActivity
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.domain.model.isScouter
import es.scoutsafa.app.domain.usecase.DeleteActivityUseCase
import es.scoutsafa.app.domain.usecase.ListActivitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActivityListViewModel(
    listActivitiesUseCase: ListActivitiesUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
    userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {

    /** null = todas las ramas */
    val selectedSection = MutableStateFlow<Section?>(null)
    val showPast = MutableStateFlow(false)

    private val allActivities = listActivitiesUseCase()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isScouter: StateFlow<Boolean> = currentUserFlow(authRepository, userRepository)
        .map { it?.isScouter() == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val activities: StateFlow<List<ScoutActivity>> = combine(
        allActivities, selectedSection, showPast
    ) { activities, section, past ->
        val now = System.currentTimeMillis()
        val filtered = activities.filter { activity ->
            val sectionOk = section == null ||
                    activity.section.isBlank() || activity.section == section.name
            val timeOk = if (past) activity.endTime < now else activity.endTime >= now
            sectionOk && timeOk
        }
        // Próximas: la más cercana primero. Pasadas: la más reciente primero.
        if (past) filtered.sortedByDescending { it.startTime } else filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(id: String) {
        viewModelScope.launch { deleteActivityUseCase(id) }
    }
}
