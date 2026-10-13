package es.scoutsafa.app.presentation.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.domain.model.isScouter
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.ui.components.SectionFilterRow
import es.scoutsafa.app.presentation.viewmodel.ProfileViewModel
import es.scoutsafa.app.presentation.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/**
 * `navController` es el del NavGraph principal: al cerrar sesión se vuelve a Login.
 */
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel(),
    // ThemeViewModel es el de la Activity (el mismo que usa MainActivity), no uno por pantalla.
    themeViewModel: ThemeViewModel = koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val user      by viewModel.currentUser.collectAsState()
    val phone     by viewModel.phone.collectAsState()
    val section   by viewModel.section.collectAsState()
    val isSaving  by viewModel.isSaving.collectAsState()
    val message   by viewModel.message.collectAsState()
    val loggedOut by viewModel.loggedOut.collectAsState()
    val isDark    by themeViewModel.isDarkTheme.collectAsState()

    LaunchedEffect(loggedOut) {
        if (loggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(message) {
        if (message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Perfil",
            actions = {
                IconButton(onClick = viewModel::logout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        user?.fullName.orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(user?.email.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (user?.isScouter() == true) "Scouter" else "Familia",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.phone.value = it },
                label = { Text("Teléfono de contacto") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Rama", style = MaterialTheme.typography.labelLarge)
            SectionFilterRow(
                selected = section,
                onSelected = { viewModel.section.value = it },
                allLabel = "Sin rama",
                contentPadding = PaddingValues(0.dp)
            )

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
                    Text("Guardar cambios")
                }
            }

            if (message != null) {
                Text(message!!, style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tema oscuro")
                Switch(checked = isDark, onCheckedChange = { themeViewModel.setDarkTheme(it) })
            }

            OutlinedButton(
                onClick = viewModel::logout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
