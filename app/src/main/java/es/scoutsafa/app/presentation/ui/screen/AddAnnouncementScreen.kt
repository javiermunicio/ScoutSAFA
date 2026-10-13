package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.ui.components.SectionFilterRow
import es.scoutsafa.app.presentation.viewmodel.AddAnnouncementViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddAnnouncementScreen(
    navController: NavController,
    viewModel: AddAnnouncementViewModel = koinViewModel()
) {
    val title        by viewModel.title.collectAsState()
    val body         by viewModel.body.collectAsState()
    val section      by viewModel.section.collectAsState()
    val pinned       by viewModel.pinned.collectAsState()
    val isSaving     by viewModel.isSaving.collectAsState()
    val saved        by viewModel.saved.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(saved) { if (saved) navController.popBackStack() }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Nuevo anuncio",
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.title.value = it; viewModel.clearError() },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = body,
                onValueChange = { viewModel.body.value = it; viewModel.clearError() },
                label = { Text("Texto del anuncio") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Destinatarios", style = MaterialTheme.typography.labelLarge)
            SectionFilterRow(
                selected = section,
                onSelected = { viewModel.section.value = it },
                allLabel = "Todo el grupo",
                contentPadding = PaddingValues(0.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Fijar en destacados")
                Switch(checked = pinned, onCheckedChange = { viewModel.pinned.value = it })
            }

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = viewModel::save,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Publicar")
                }
            }
        }
    }
}
