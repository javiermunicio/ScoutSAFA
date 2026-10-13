package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.scoutsafa.app.presentation.navigation.Screen

/**
 * Contenedor con la barra inferior y su propio NavHost.
 * `navController` es el del NavGraph principal (se usa para cerrar sesión);
 * `innerNavController` navega entre las pestañas y sus pantallas de detalle.
 */
@Composable
fun HomeScreen(navController: NavController) {
    val innerNavController = rememberNavController()
    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val tabs = listOf(
        Triple("Inicio",      Icons.Filled.Home,      Screen.Dashboard.route),
        Triple("Actividades", Icons.Filled.Event,     Screen.Activities.route),
        Triple("Anuncios",    Icons.Filled.Campaign,  Screen.Announcements.route),
        Triple("Perfil",      Icons.Filled.Person,    Screen.Profile.route)
    )

    val selectedTab = when {
        currentRoute == null -> 0
        currentRoute.contains("activit") -> 1
        currentRoute.contains("announcement") -> 2
        currentRoute == Screen.Profile.route -> 3
        else -> 0
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor   = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, (label, icon, route) ->
                    NavigationBarItem(
                        icon     = { Icon(icon, contentDescription = label) },
                        label    = { Text(label) },
                        selected = selectedTab == index,
                        onClick  = {
                            innerNavController.navigate(route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = innerNavController,
            startDestination = Screen.Dashboard.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = innerNavController)
            }
            composable(Screen.Activities.route) {
                ActivityListScreen(navController = innerNavController)
            }
            composable(Screen.NewActivity.route) {
                AddActivityScreen(navController = innerNavController)
            }
            composable(Screen.ActivityDetail.route) { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
                ActivityDetailScreen(activityId = activityId, navController = innerNavController)
            }
            composable(Screen.EditActivity.route) { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
                EditActivityScreen(activityId = activityId, navController = innerNavController)
            }
            composable(Screen.Announcements.route) {
                AnnouncementListScreen(navController = innerNavController)
            }
            composable(Screen.NewAnnouncement.route) {
                AddAnnouncementScreen(navController = innerNavController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
        }
    }
}
