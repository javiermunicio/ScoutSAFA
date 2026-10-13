package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.domain.model.sectionOrNull
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.ui.components.ActivityCard
import es.scoutsafa.app.presentation.ui.components.AnnouncementCard
import es.scoutsafa.app.presentation.ui.components.EmptyState
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.ui.components.SectionBadge
import es.scoutsafa.app.presentation.viewmodel.DashboardViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val user          by viewModel.currentUser.collectAsState()
    val activities    by viewModel.upcomingActivities.collectAsState()
    val announcements by viewModel.pinnedAnnouncements.collectAsState()

    val firstName = user?.fullName?.substringBefore(" ").orEmpty()
    val section = user?.sectionOrNull()

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(title = "Scouts SAFA")

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (firstName.isBlank()) "¡Siempre listos!" else "¡Siempre listos, $firstName!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (section != null) {
                        SectionBadge(section)
                    } else {
                        Text(
                            "Elige tu rama en el perfil para ver solo lo que te afecta.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Próximas actividades",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        navController.navigate(Screen.Activities.route) { launchSingleTop = true }
                    }) { Text("Ver todas") }
                }
            }
            if (activities.isEmpty()) {
                item { EmptyState(Icons.Filled.Event, "No hay actividades próximas") }
            } else {
                items(activities, key = { it.id }) { activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = {
                            navController.navigate(Screen.ActivityDetail.createRoute(activity.id))
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Anuncios destacados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        navController.navigate(Screen.Announcements.route) { launchSingleTop = true }
                    }) { Text("Ver todos") }
                }
            }
            if (announcements.isEmpty()) {
                item { EmptyState(Icons.Filled.Campaign, "No hay anuncios destacados") }
            } else {
                items(announcements, key = { it.id }) { announcement ->
                    AnnouncementCard(announcement = announcement, maxBodyLines = 3)
                }
            }
        }
    }
}
