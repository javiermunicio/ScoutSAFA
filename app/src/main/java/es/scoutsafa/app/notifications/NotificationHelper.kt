package es.scoutsafa.app.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.scoutsafa.app.MainActivity
import es.scoutsafa.app.R

object NotificationHelper {
    const val CHANNEL_ANNOUNCEMENTS = "channel_announcements"
    const val CHANNEL_ACTIVITIES    = "channel_activities"

    private const val NOTIF_ANNOUNCEMENT = 1001
    private const val NOTIF_ACTIVITY     = 1002

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ANNOUNCEMENTS,
                    "Anuncios",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Avisos de los scouters al grupo o a tu rama"
                    enableVibration(true)
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ACTIVITIES,
                    "Actividades",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Nuevas actividades y cambios en las existentes"
                }
            )
        }
    }

    private fun mainPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showAnnouncementNotification(context: Context, title: String, body: String) =
        show(context, CHANNEL_ANNOUNCEMENTS, NOTIF_ANNOUNCEMENT, title, body,
            NotificationCompat.PRIORITY_HIGH)

    fun showActivityNotification(context: Context, title: String, body: String) =
        show(context, CHANNEL_ACTIVITIES, NOTIF_ACTIVITY, title, body,
            NotificationCompat.PRIORITY_DEFAULT)

    // El permiso POST_NOTIFICATIONS se comprueba con areNotificationsEnabled().
    @SuppressLint("MissingPermission")
    private fun show(
        context: Context,
        channel: String,
        id: Int,
        title: String,
        body: String,
        priority: Int
    ) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent(context))
            .build()

        manager.notify(id, notification)
    }
}
