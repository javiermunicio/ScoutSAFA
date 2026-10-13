package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.domain.model.ScoutActivity
import es.scoutsafa.app.domain.usecase.GetActivityByIdUseCase
import es.scoutsafa.app.domain.usecase.UpdateActivityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditActivityViewModel(
    private val getActivityByIdUseCase: GetActivityByIdUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase
) : ViewModel() {

    private var original: ScoutActivity? = null

    private val _form = MutableStateFlow(ActivityFormState())
    val form: StateFlow<ActivityFormState> = _form

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Carga la actividad solo la primera vez, para no pisar lo que el usuario ya ha editado. */
    fun load(id: String) {
        if (original != null) return
        viewModelScope.launch {
            getActivityByIdUseCase(id).fold(
                onSuccess = {
                    original = it
                    _form.value = ActivityFormState.from(it)
                },
                onFailure = { _errorMessage.value = "No se pudo cargar la actividad" }
            )
            _isLoading.value = false
        }
    }

    fun onFormChange(newForm: ActivityFormState) {
        _form.value = newForm
        _errorMessage.value = null
    }

    fun save() {
        val base = original ?: return
        _form.value.validate()?.let { _errorMessage.value = it; return }
        viewModelScope.launch {
            _isSaving.value = true
            val ok = updateActivityUseCase(_form.value.toActivity(base))
            if (ok) _saved.value = true else _errorMessage.value = "No se pudo guardar los cambios"
            _isSaving.value = false
        }
    }
}
