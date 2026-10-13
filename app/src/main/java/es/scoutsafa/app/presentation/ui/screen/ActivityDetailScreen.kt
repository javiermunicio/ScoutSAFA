package es.scoutsafa.app.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.scoutsafa.app.domain.model.AttendanceStatus
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.presentation.navigation.Screen
import es.scoutsafa.app.presentation.ui.components.EmptyState
import es.scoutsafa.app.presentation.ui.components.ScreenHeader
import es.scoutsafa.app.presentation.ui.components.SectionBadge
import es.scoutsafa.app.presentation.viewmodel.ActivityDetailViewModel
import es.scoutsafa.app.util.DateUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActivityDetailScreen(
    activityId: String,
    navController: NavController,
    viewModel: ActivityDetailViewModel = koinViewModel()
) {
    val activity    by viewModel.activity.collectAsState()
    val isLoading   by viewModel.isLoading.collectAsState()
    val deleted    by viewModel.deleted.collectAsState()
    val attendances by viewModel.attendances.collectAsState()
    val myStatus    by viewModel.myStatus.collectAsState()
    val isScouter   by viewModel.isScouter.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Se vuelve a ejecutar al volver de editar, así se refrescan los datos.
    LaunchedEffect(activityId) { viewModel.load(activityId) }
    LaunchedEffect(deleted) { if (deleted) navController.popBackStack() }

    val current = activity
    val confirmed = attendances.filter { it.status == AttendanceStatus.CONFIRMED.name }
    val declined  = attendances.filter { it.status == AttendanceStatus.DECLINED.name }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = current?.title ?: "Actividad",
            onBack = { navController.popBackStack() },
            actions = {
                if (isScouter && current != null) {
                    IconButton(onClick = {
                        navController.navigate(Screen.EditActivity.createRoute(current.id))
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )

        when {
            isLoading && current == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            current == null -> {
                EmptyState(Icons.Filled.Event, "No se ha encontrado la actividad")
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionBadge(Section.fromName(current.section))

                    InfoRow(Icons.Filled.Schedule, "Inicio", DateUtils.formatDateTime(current.startTime))
                    InfoRow(Icons.Filled.Schedule, "Fin", DateUtils.formatDateTime(current.endTime))
                    if (current.location.isNotBlank()) {
                        InfoRow(Icons.Filled.LocationOn, "Lugar", current.location)
                    }
                    if (current.meetingPoint.isNotBlank()) {
                        InfoRow(Icons.Filled.Place, "Punto de encuentro", current.meetingPoint)
                    }

                    if (current.description.isNotBlank()) {
                        HorizontalDivider()
                        Text(current.description, style = MaterialTheme.typography.bodyLarge)
                    }

                    HorizontalDivider()
                    Text(
                        "¿Asistirás?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val confirmedSelected = myStatus == AttendanceStatus.CONFIRMED.name
                        val declinedSelected  = myStatus == AttendanceStatus.DECLINED.name

                        if (confirmedSelected) {
                            Button(
                                onClick = { viewModel.setStatus(AttendanceStatus.CONFIRMED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Check, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Voy")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.setStatus(AttendanceStatus.CONFIRMED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Check, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Voy")
                            }
                        }

                        if (declinedSelected) {
                            Button(
                                onClick = { viewModel.setStatus(AttendanceStatus.DECLINED) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Close, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("No voy")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.setStatus(AttendanceStatus.DECLINED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Close, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("No voy")
                            }
                        }
                    }

                    Text(
                        "${confirmed.size} confirmados · ${declined.size} no asisten",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Solo los scouters ven los nombres, para no exponer datos de otras familias.
                    if (isScouter && attendances.isNotEmpty()) {
                        if (confirmed.isNotEmpty()) {
                            Text("Confirmados", fontWeight = FontWeight.Bold)
                            confirmed.forEach { Text("• ${it.userName}") }
                        }
                        if (declined.isNotEmpty()) {
                            Text("No asisten", fontWeight = FontWeight.Bold)
                            declined.forEach { Text("• ${it.userName}") }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar actividad") },
            text = { Text("Se borrará la actividad para todo el grupo. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
