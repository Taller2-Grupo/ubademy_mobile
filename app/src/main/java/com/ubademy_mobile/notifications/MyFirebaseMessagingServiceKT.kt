package com.ubademy_mobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.Chat2Activity
import com.ubademy_mobile.activities.MessagingActivity

const val channelId = "notification_channel"
const val channelName = "com.ubademy_mobile.notifications"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var notificationId: Int = 0

    override fun onMessageReceived(p0: RemoteMessage) {
        if (p0.notification != null) {
            Log.d("notificacion", p0.notification!!.title!! + "  " + p0.notification!!.body!!)
            generateNotification(p0.notification!!.title!!, p0.notification!!.body!!)
        }
    }

    fun getRemoteView(title: String, body: String): RemoteViews {
        val remoteView = RemoteViews(applicationContext.packageName, R.layout.notificacion)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.description, body)
        remoteView.setImageViewResource(R.id.logo, R.mipmap.ic_launcher)

        Log.d("remoteview", "aaaaaaaa")
        return remoteView
    }

    fun generateNotification(title: String, body: String){
        val intent = Intent(this, Chat2Activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ubademy).setAutoCancel(true).setOnlyAlertOnce(true).setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, body))

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("if", "bbbbbbbb")
            val notificacionChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificacionChannel)
        }

        notificationManager.notify(notificationId, builder.build())
        notificationId += 1
    }

}