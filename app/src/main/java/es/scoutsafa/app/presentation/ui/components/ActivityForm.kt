package es.scoutsafa.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.scoutsafa.app.presentation.viewmodel.ActivityFormState

/** Formulario compartido por las pantallas de crear y editar actividad. */
@Composable
fun ActivityForm(
    form: ActivityFormState,
    onFormChange: (ActivityFormState) -> Unit,
    errorMessage: String?,
    isSaving: Boolean,
    submitLabel: String,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = form.title,
            onValueChange = { onFormChange(form.copy(title = it)) },
            label = { Text("Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.description,
            onValueChange = { onFormChange(form.copy(description = it)) },
            label = { Text("Descripción (qué llevar, material...)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Rama", style = MaterialTheme.typography.labelLarge)
        SectionFilterRow(
            selected = form.section,
            onSelected = { onFormChange(form.copy(section = it)) },
            allLabel = "Todo el grupo",
            contentPadding = PaddingValues(0.dp)
        )

        OutlinedTextField(
            value = form.location,
            onValueChange = { onFormChange(form.copy(location = it)) },
            label = { Text("Lugar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.meetingPoint,
            onValueChange = { onFormChange(form.copy(meetingPoint = it)) },
            label = { Text("Punto de encuentro") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = form.startDate,
                onValueChange = { onFormChange(form.copy(startDate = it)) },
                label = { Text("Inicio (DD/MM/AAAA)") },
                singleLine = true,
                modifier = Modifier.weight(1.6f)
            )
            OutlinedTextField(
                value = form.startTime,
                onValueChange = { onFormChange(form.copy(startTime = it)) },
                label = { Text("Hora") },
                placeholder = { Text("HH:MM") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = form.endDate,
                onValueChange = { onFormChange(form.copy(endDate = it)) },
                label = { Text("Fin (DD/MM/AAAA)") },
                singleLine = true,
                modifier = Modifier.weight(1.6f)
            )
            OutlinedTextField(
                value = form.endTime,
                onValueChange = { onFormChange(form.copy(endTime = it)) },
                label = { Text("Hora") },
                placeholder = { Text("HH:MM") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onSubmit,
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
                Text(submitLabel)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
