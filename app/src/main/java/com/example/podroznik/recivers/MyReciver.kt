package com.example.podroznik.recivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.podroznik.R
import com.example.podroznik.views.MainActivity
import com.example.podroznik.views.NoteActivity
import com.google.android.gms.location.GeofencingEvent

class MyReciver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event.hasError()){
            Log.e("BLAD","Blad lokalizacji")
            return
        }
        val geofence by lazy {event.triggeringGeofences[0]}

        val notificationMenager = (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationMenager.createNotificationChannel(NotificationChannel(
            context.resources.getString(R.string.notification_channel),
            "MyChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        ))

        val photo = geofence.requestId

        Log.e("TEST:",""+photo.toString())
        Log.e("TEST1","to juz jest")

        val newIntent = Intent(context,NoteActivity::class.java)
        newIntent.putExtra("photo",photo)

        context.getSystemService(NotificationManagerCompat::class.java).apply {
            val requestID = System.currentTimeMillis().toInt()
            val pi = PendingIntent.getActivity(context, requestID, newIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(context, context.resources.getString(R.string.notification_channel))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Zobacz Wspomnienie")
                .setContentText("Jesteś niedaleko miejsca")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build()
            notificationMenager.notify(1, notification)
        }
    }
}