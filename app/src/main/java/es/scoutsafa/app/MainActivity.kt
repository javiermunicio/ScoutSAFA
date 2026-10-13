package es.scoutsafa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.notifications.RequestNotificationPermission
import es.scoutsafa.app.presentation.navigation.NavGraph
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.viewmodel.ThemeViewModel
import es.scoutsafa.app.ui.theme.ScoutSAFATheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository: AuthRepository by inject()

        setContent {
            val isDark by themeViewModel.isDarkTheme.collectAsState()

            ScoutSAFATheme(darkTheme = isDark) {
                RequestNotificationPermission()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = if (authRepository.isLoggedIn) {
                        Screen.Home.route
                    } else {
                        Screen.Login.route
                    }
                    NavGraph(
                        navController    = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
