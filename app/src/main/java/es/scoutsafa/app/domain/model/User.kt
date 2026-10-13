package es.scoutsafa.app.domain.model

enum class UserRole { FAMILIA, SCOUTER }

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    /** Valor de [UserRole]. Todos se registran como FAMILIA; los scouters se promocionan desde Firestore. */
    val role: String = UserRole.FAMILIA.name,
    /** Valor de [Section] ("" si aún no la ha elegido). */
    val section: String = "",
    val onboardingDone: Boolean = false,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val fcmToken: String = ""
)

fun User.isScouter(): Boolean = role == UserRole.SCOUTER.name

fun User.sectionOrNull(): Section? = Section.fromName(section)
