package es.scoutsafa.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.scoutsafa.app.domain.model.Announcement
import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.util.DateUtils

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    modifier: Modifier = Modifier,
    canDelete: Boolean = false,
    onDelete: () -> Unit = {},
    maxBodyLines: Int = Int.MAX_VALUE
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (announcement.pinned) {
                    Icon(
                        Icons.Filled.PushPin,
                        contentDescription = "Fijado",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                SectionBadge(Section.fromName(announcement.section))
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Eliminar anuncio",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = maxBodyLines,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${announcement.authorName} · ${DateUtils.formatDateTime(announcement.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
