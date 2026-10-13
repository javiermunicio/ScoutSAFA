package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import es.scoutsafa.app.presentation.ui.components.ActivityForm
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.viewmodel.EditActivityViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditActivityScreen(
    activityId: String,
    navController: NavController,
    viewModel: EditActivityViewModel = koinViewModel()
) {
    val form         by viewModel.form.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val isSaving     by viewModel.isSaving.collectAsState()
    val saved        by viewModel.saved.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(activityId) { viewModel.load(activityId) }
    LaunchedEffect(saved) { if (saved) navController.popBackStack() }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Editar actividad",
            onBack = { navController.popBackStack() }
        )
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            ActivityForm(
                form = form,
                onFormChange = viewModel::onFormChange,
                errorMessage = errorMessage,
                isSaving = isSaving,
                submitLabel = "Guardar cambios",
                onSubmit = viewModel::save
            )
        }
    }
}
