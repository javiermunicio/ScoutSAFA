package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.ScoutActivity
import es.scoutsafa.app.domain.model.User
import es.scoutsafa.app.domain.model.isScouter
import es.scoutsafa.app.domain.usecase.AddActivityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddActivityViewModel(
    private val addActivityUseCase: AddActivityUseCase,
    userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val currentUser: StateFlow<User?> = currentUserFlow(authRepository, userRepository)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _form = MutableStateFlow(ActivityFormState())
    val form: StateFlow<ActivityFormState> = _form

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onFormChange(newForm: ActivityFormState) {
        _form.value = newForm
        _errorMessage.value = null
    }

    fun save() {
        viewModelScope.launch {
            val user = currentUser.value
            if (user == null || !user.isScouter()) {
                _errorMessage.value = "Solo los scouters pueden crear actividades"
                return@launch
            }
            _form.value.validate()?.let { _errorMessage.value = it; return@launch }

            _isSaving.value = true
            val ok = addActivityUseCase(_form.value.toActivity(ScoutActivity(createdBy = user.id)))
            if (ok) _saved.value = true else _errorMessage.value = "No se pudo guardar la actividad"
            _isSaving.value = false
        }
    }
}
