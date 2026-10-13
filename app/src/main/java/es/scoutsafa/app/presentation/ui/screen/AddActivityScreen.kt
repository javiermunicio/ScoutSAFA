package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import es.scoutsafa.app.presentation.ui.components.ActivityForm
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.viewmodel.AddActivityViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddActivityScreen(
    navController: NavController,
    viewModel: AddActivityViewModel = koinViewModel()
) {
    val form         by viewModel.form.collectAsState()
    val isSaving     by viewModel.isSaving.collectAsState()
    val saved        by viewModel.saved.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(saved) { if (saved) navController.popBackStack() }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Nueva actividad",
            onBack = { navController.popBackStack() }
        )
        ActivityForm(
            form = form,
            onFormChange = viewModel::onFormChange,
            errorMessage = errorMessage,
            isSaving = isSaving,
            submitLabel = "Crear actividad",
            onSubmit = viewModel::save
        )
    }
}
