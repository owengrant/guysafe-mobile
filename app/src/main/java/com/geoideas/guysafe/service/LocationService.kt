package com.geoideas.guysafe.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.geoideas.guysafe.location.Locator
import com.geoideas.guysafe.model.repository.AppRepository
import com.google.android.gms.maps.model.LatLng
import kotlin.concurrent.thread
import android.app.NotificationChannel
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.graphics.Color
import com.geoideas.guysafe.R


class LocationService : Service() {

    private val NOTICE_CHANNEL_ID = "com.geoideas.guysafe"
    private val SERVICE_NOTICE_ID = 1000
    private val ALERT_NOTICE_ID = 2000

    private lateinit var notificationManager: NotificationManager
    private lateinit var repo: AppRepository

    override fun onCreate() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificatioChannel()
        repo = AppRepository(this)
        startForeground(SERVICE_NOTICE_ID, createNotice().build())
        startTracker()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    private fun createNotificatioChannel() {
        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTICE_CHANNEL_ID,
                "GuySafe", NotificationManager
                    .IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private fun createNotice(title: String = "GuySafe", text: String = "Keep Guyana Safe") =
        NotificationCompat.Builder(this, NOTICE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

    private fun startTracker() {
        Locator(this).startTracking {
            thread {
                try {
                    repo.refreshZones()
                } catch (e: Exception) {
                    Log.e("LocationService", "refreshZones error")
                }
                val inZones = repo.readZones()
                    .filter { zone ->
                        val zoneLocation = Location("").apply {
                            latitude = zone.latitude
                            longitude = zone.longitude
                        }
                        it.distanceTo(zoneLocation) >= zone.radius
                    }
                if(inZones.isNotEmpty()) {
                    val text = "Clean your hands, and don't touch your face."
                    val notice = createNotice("Danger Zone Detected", text).setAutoCancel(true).build()
                    notificationManager.notify(ALERT_NOTICE_ID, notice)
                }
            }
        }
    }

//    .setContentText(text)
//    .setSmallIcon(R.drawable.ic_logo_notice)
//    .setPriority(NotificationCompat.PRIORITY_HIGH)
//    .setDefaults(NotificationCompat.DEFAULT_ALL)
//    .setAutoCancel(true)
}