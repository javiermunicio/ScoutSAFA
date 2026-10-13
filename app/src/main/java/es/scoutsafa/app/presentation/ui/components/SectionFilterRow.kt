package es.scoutsafa.app.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.scoutsafa.app.domain.model.Section

/**
 * Fila de chips para elegir rama. El primer chip representa "ninguna" (selected == null),
 * y se puede renombrar con [allLabel] ("Todas" en filtros, "Todo el grupo" en formularios).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionFilterRow(
    selected: Section?,
    onSelected: (Section?) -> Unit,
    modifier: Modifier = Modifier,
    allLabel: String = "Todas",
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelected(null) },
                label = { Text(allLabel) }
            )
        }
        items(Section.entries) { section ->
            FilterChip(
                selected = selected == section,
                onClick = { onSelected(if (selected == section) null else section) },
                label = { Text(section.displayName) }
            )
        }
    }
}
