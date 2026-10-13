package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.ui.components.ActivityCard
import es.scoutsafa.app.presentation.ui.components.EmptyState
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.ui.components.SectionFilterRow
import es.scoutsafa.app.presentation.viewmodel.ActivityListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    navController: NavController,
    viewModel: ActivityListViewModel = koinViewModel()
) {
    val activities      by viewModel.activities.collectAsState()
    val selectedSection by viewModel.selectedSection.collectAsState()
    val showPast        by viewModel.showPast.collectAsState()
    val isScouter       by viewModel.isScouter.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Actividades")

            Spacer(Modifier.height(8.dp))
            SectionFilterRow(
                selected = selectedSection,
                onSelected = { viewModel.selectedSection.value = it }
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showPast,
                    onClick = { viewModel.showPast.value = false },
                    label = { Text("Próximas") }
                )
                FilterChip(
                    selected = showPast,
                    onClick = { viewModel.showPast.value = true },
                    label = { Text("Pasadas") }
                )
            }

            if (activities.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Event,
                    text = if (showPast) "No hay actividades pasadas" else "No hay actividades próximas",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activities, key = { it.id }) { activity ->
                        ActivityCard(
                            activity = activity,
                            onClick = {
                                navController.navigate(Screen.ActivityDetail.createRoute(activity.id))
                            }
                        )
                    }
                }
            }
        }

        if (isScouter) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.NewActivity.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva actividad")
            }
        }
    }
}
