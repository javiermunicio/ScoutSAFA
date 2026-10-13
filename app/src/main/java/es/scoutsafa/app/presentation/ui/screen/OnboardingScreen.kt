package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.ui.components.sectionColor
import es.scoutsafa.app.presentation.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val phone        by viewModel.phone.collectAsState()
    val section      by viewModel.section.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val saved        by viewModel.saved.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    fun goHome() {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Onboarding.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(saved) { if (saved) goHome() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "¡Bienvenido/a al grupo!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Elige la rama de tu hijo/a (o la tuya si eres scouter) para ver solo las actividades y avisos que te afectan. Puedes cambiarla luego en tu perfil.",
            style = MaterialTheme.typography.bodyMedium
        )

        Section.entries.forEach { option ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onSectionSelected(if (section == option) null else option) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(selected = section == option, onClick = null)
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(sectionColor(option), CircleShape)
                    )
                    Column {
                        Text(option.displayName, fontWeight = FontWeight.Bold)
                        Text(option.ages, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        OutlinedTextField(
            value = phone,
            onValueChange = viewModel::onPhoneChanged,
            label = { Text("Teléfono de contacto (opcional)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = viewModel::save,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Continuar")
            }
        }
        TextButton(
            onClick = { goHome() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Omitir por ahora")
        }
    }
}
