package es.scoutsafa.app.di

import es.scoutsafa.app.data.repositories.*
import es.scoutsafa.app.domain.usecase.*
import es.scoutsafa.app.notifications.FcmTokenManager
import es.scoutsafa.app.presentation.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseMessaging.getInstance() }

    // Repositories
    single { AuthRepository(get()) }
    single { UserRepository(get()) }
    single { ActivityRepository(get()) }
    single { AnnouncementRepository(get()) }
    single { AttendanceRepository(get()) }

    // Notifications
    single { FcmTokenManager(get(), get()) }

    // Use cases
    factory { ListActivitiesUseCase(get()) }
    factory { GetActivityByIdUseCase(get()) }
    factory { AddActivityUseCase(get()) }
    factory { UpdateActivityUseCase(get()) }
    factory { DeleteActivityUseCase(get()) }
    factory { ListAnnouncementsUseCase(get()) }
    factory { AddAnnouncementUseCase(get()) }
    factory { DeleteAnnouncementUseCase(get()) }
    factory { ObserveAttendanceUseCase(get()) }
    factory { SetAttendanceUseCase(get()) }
    factory { SaveProfileUseCase(get()) }

    // ViewModels
    viewModel { ThemeViewModel(androidContext()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
    viewModel { OnboardingViewModel(get(), get(), get()) }
    viewModel { DashboardViewModel(get(), get(), get(), get()) }
    viewModel { ActivityListViewModel(get(), get(), get(), get()) }
    viewModel { ActivityDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AddActivityViewModel(get(), get(), get()) }
    viewModel { EditActivityViewModel(get(), get()) }
    viewModel { AnnouncementListViewModel(get(), get(), get(), get()) }
    viewModel { AddAnnouncementViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
}
