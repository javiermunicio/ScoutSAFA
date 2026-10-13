package es.scoutsafa.app.domain.model

/**
 * Ramas (secciones) del grupo scout. Se guardan en Firestore por su `name`.
 */
enum class Section(val displayName: String, val ages: String) {
    CASTORES("Castores", "6-8 años"),
    LOBATOS("Lobatos y Lobatas", "8-11 años"),
    SCOUTS("Scouts", "11-14 años"),
    ESCULTAS("Escultas", "14-17 años"),
    ROVERS("Rovers", "17-21 años");

    companion object {
        fun fromName(name: String): Section? = entries.firstOrNull { it.name == name }
    }
}

/**
 * ¿Debe ver este usuario un elemento (actividad/anuncio) de la rama [itemSection]?
 * Sí si el elemento es para todo el grupo (""), si el usuario no tiene rama (p. ej. un scouter
 * que ve todo) o si coinciden.
 */
fun sectionVisibleFor(itemSection: String, userSection: String): Boolean =
    itemSection.isBlank() || userSection.isBlank() || itemSection == userSection
