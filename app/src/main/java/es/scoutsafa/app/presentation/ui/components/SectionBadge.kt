package es.scoutsafa.app.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.ui.theme.*

fun sectionColor(section: Section?): Color = when (section) {
    Section.CASTORES -> SectionCastores
    Section.LOBATOS  -> SectionLobatos
    Section.SCOUTS   -> SectionScouts
    Section.ESCULTAS -> SectionEscultas
    Section.ROVERS   -> SectionRovers
    null             -> SectionGroup
}

/** Etiqueta con el color de la rama. `null` se muestra como "Todo el grupo". */
@Composable
fun SectionBadge(section: Section?, modifier: Modifier = Modifier) {
    val background = sectionColor(section)
    val textColor = if (background.luminance() > 0.5f) Color.Black else Color.White
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = section?.displayName ?: "Todo el grupo",
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
