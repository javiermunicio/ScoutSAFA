package es.scoutsafa.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")

    // Pestañas de la barra inferior
    data object Dashboard : Screen("dashboard")
    data object Activities : Screen("activities")
    data object Announcements : Screen("announcements")
    data object Profile : Screen("profile")

    data object NewActivity : Screen("new_activity")
    data object NewAnnouncement : Screen("new_announcement")

    data object ActivityDetail : Screen("activity_detail/{activityId}") {
        fun createRoute(activityId: String) = "activity_detail/$activityId"
    }

    data object EditActivity : Screen("edit_activity/{activityId}") {
        fun createRoute(activityId: String) = "edit_activity/$activityId"
    }
}
