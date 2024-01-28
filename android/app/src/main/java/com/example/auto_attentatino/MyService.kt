package com.example.auto_attentatino

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.auto_attentatino.api.service.Api
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class MyService : FirebaseMessagingService() {
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    var notificationId = 1

    val api = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_URL)
//        .baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(Api::class.java)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Okane", "token : $token")
        api.sendToken(token).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
            }

        } )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Okane", "message : ${message.data}")
        val title = message.data["title"]
        val body = message.data["body"]

        if (title != null && body != null) {
            showNotification(title, body)
        }

    }
    private fun showNotification(title: String, body: String) {
//        turnScreenOn()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val audioAttributes =
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build()

        // Android 8.0 이상에서는 Notification 채널을 등록해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel",
                "My Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setDescription("자동 출첵!!");
            channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hwik), audioAttributes);
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "my_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setGroup("groupId")
            .setVibrate(longArrayOf(0, 500, 1000))
            .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hwik))
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val groupBuilder = NotificationCompat.Builder(this, "my_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup("groupId")
            .setGroupSummary(true)

        // Notification을 표시
        notificationId += 1
        notificationManager.notify(notificationId, builder.build())
        notificationManager.notify(1234, groupBuilder.build())
    }
    private fun turnScreenOn() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager?
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "MyApp:WakeLock"
        )
        wakeLock?.acquire(4 * 1000L /* 10 minutes */)
    }
}