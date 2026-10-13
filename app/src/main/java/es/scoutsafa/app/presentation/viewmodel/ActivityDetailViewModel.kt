package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.ActivityAttendance
import es.scoutsafa.app.domain.model.AttendanceStatus
import es.scoutsafa.app.domain.model.ScoutActivity
import es.scoutsafa.app.domain.model.User
import es.scoutsafa.app.domain.model.isScouter
import es.scoutsafa.app.domain.usecase.DeleteActivityUseCase
import es.scoutsafa.app.domain.usecase.GetActivityByIdUseCase
import es.scoutsafa.app.domain.usecase.ObserveAttendanceUseCase
import es.scoutsafa.app.domain.usecase.SetAttendanceUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActivityDetailViewModel(
    private val getActivityByIdUseCase: GetActivityByIdUseCase,
    private val observeAttendanceUseCase: ObserveAttendanceUseCase,
    private val setAttendanceUseCase: SetAttendanceUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
    userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val currentUser: StateFlow<User?> = currentUserFlow(authRepository, userRepository)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _activity = MutableStateFlow<ScoutActivity?>(null)
    val activity: StateFlow<ScoutActivity?> = _activity

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted

    private val _attendances = MutableStateFlow<List<ActivityAttendance>>(emptyList())
    val attendances: StateFlow<List<ActivityAttendance>> = _attendances

    val isScouter: StateFlow<Boolean> = currentUser
        .map { it?.isScouter() == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /** Estado de asistencia de este usuario: CONFIRMED, DECLINED o null si no ha respondido. */
    val myStatus: StateFlow<String?> = combine(_attendances, currentUser) { list, user ->
        list.firstOrNull { it.userId == user?.id }?.status
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private var attendanceJob: Job? = null
    private var observedId: String? = null

    /** Se llama cada vez que la pantalla aparece, así se refresca tras editar la actividad. */
    fun load(id: String) {
        viewModelScope.launch {
            // Si falla (p. ej. la actividad se borró), _activity queda en null y la pantalla lo indica.
            getActivityByIdUseCase(id).onSuccess { _activity.value = it }
            _isLoading.value = false
        }
        if (observedId != id) {
            observedId = id
            attendanceJob?.cancel()
            attendanceJob = viewModelScope.launch {
                observeAttendanceUseCase(id)
                    .catch { emit(emptyList()) }
                    .collect { _attendances.value = it }
            }
        }
    }

    fun setStatus(status: AttendanceStatus) {
        val id = _activity.value?.id ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch { setAttendanceUseCase(id, user, status) }
    }

    fun delete() {
        val id = _activity.value?.id ?: return
        viewModelScope.launch {
            if (deleteActivityUseCase(id)) _deleted.value = true
        }
    }
}
