package dev.keader.correiostracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks

class LocalNotification {
    companion object {
        private val CHANNEL_ID = "track_update_channel"
        private val NOTIFICATION_ID = 768

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun sendNotification(context: Context, item: ItemWithTracks) {
            val args = Bundle().apply { putString("trackCode", item.item.code) }
            val pendingIntent = NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.action_global_trackDetailFragment)
                    .setArguments(args)
                    .createPendingIntent()

            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.delivery_man)
            val lastTrack = item.tracks.first()
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(bitmap)
                    .setContentTitle(context.getString(R.string.notification_format, item.item.name))
                    .setContentText(item.tracks.first().status)
                    .apply {
                        val observation = lastTrack.observation
                        if (observation.isNotEmpty()) {
                            val locale = context.getString(R.string.locale_format, lastTrack.locale)
                            val bigTextArgs = arrayOf(lastTrack.status, observation, locale)
                            val bigText = context.getString(R.string.notification_body_format,*bigTextArgs)
                            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                        }
                    }
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }

        fun sendNotificationTest(context: Context) {
            val args = Bundle().apply { putString("trackCode", "lb140562449hk".toUpperCase()) }
            val pendingIntent = NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.trackDetailFragment)
                    .setArguments(args)
                    .createPendingIntent()

            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.delivery_man)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(bitmap)
                    .setContentTitle(context.getString(R.string.notification_format, "PC da China"))
                    .setContentText("Objeto em trânsito - por favor aguarde")
                    .apply {
                        val status = "Objeto em trânsito - por favor aguarde"
                        val observation = "de País em HONG KONG / para País em Unidade de Tratamento Internacional / BR"
                        val locale = context.getString(R.string.locale_format, "HONG KONG")
                        setStyle(NotificationCompat.BigTextStyle().bigText("$status: $observation.\n$locale"))
                    }
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        }
    }
}
