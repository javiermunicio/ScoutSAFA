package es.scoutsafa.app.domain.model

data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    /** Valor de [Section]; "" significa que es para todo el grupo. */
    val section: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val pinned: Boolean = false,
    val createdAt: Long = 0
)
