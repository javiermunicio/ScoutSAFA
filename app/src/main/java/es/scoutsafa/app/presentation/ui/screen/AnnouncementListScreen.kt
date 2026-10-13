package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.ui.components.AnnouncementCard
import es.scoutsafa.app.presentation.ui.components.EmptyState
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.ui.components.SectionFilterRow
import es.scoutsafa.app.presentation.viewmodel.AnnouncementListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnnouncementListScreen(
    navController: NavController,
    viewModel: AnnouncementListViewModel = koinViewModel()
) {
    val announcements   by viewModel.announcements.collectAsState()
    val selectedSection by viewModel.selectedSection.collectAsState()
    val isScouter       by viewModel.isScouter.collectAsState()

    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Anuncios")

            Spacer(Modifier.height(8.dp))
            SectionFilterRow(
                selected = selectedSection,
                onSelected = { viewModel.selectedSection.value = it }
            )

            if (announcements.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Campaign,
                    text = "No hay anuncios",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(announcements, key = { it.id }) { announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            canDelete = isScouter,
                            onDelete = { pendingDeleteId = announcement.id }
                        )
                    }
                }
            }
        }

        if (isScouter) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.NewAnnouncement.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo anuncio")
            }
        }
    }

    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Eliminar anuncio") },
            text = { Text("Se borrará para todo el grupo. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(id)
                    pendingDeleteId = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
}
