package com.example.cnnimkar.alcogaitk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import kotlinx.android.synthetic.main.avatar_screen.*

import android.app.PendingIntent
import android.widget.Toast


class GaitService : Service(), SensorEventListener {


    lateinit var sensorManager : SensorManager
     var showNotification = false


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.values!![0] > 6 || event?.values!![0] < -6){
            Toast.makeText(this,"Abnormal Gait Detected",Toast.LENGTH_SHORT).show()
            showNotification = true
            var gaitChannel : NotificationChannel = NotificationChannel("com.example.cnnimkar.alcogaitk","GaitChannel",NotificationManager.IMPORTANCE_DEFAULT)
            gaitChannel.enableLights(true)
            gaitChannel.enableVibration(true)
            gaitChannel.lightColor = Color.GREEN

             var mManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mManager.createNotificationChannel(gaitChannel)

            var intent : Intent = Intent(this, BreathalyzerAvatar::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            var builder : Notification.Builder = Notification.Builder(getApplicationContext(), "com.example.cnnimkar.alcogaitk")
                    .setContentTitle("Take Test")
                    .setContentText("You appear to be drunk")
                    .setSmallIcon(R.drawable.navigation_empty_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            mManager.notify(101, builder.build())

        }


    }




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)



        return Service.START_STICKY
    }

    override fun onCreate() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var notification : Notification = NotificationCompat.Builder(this, "chan")
                .setContentTitle("")
                .setContentText("").build()
        startForeground(1, notification)


    }


    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}
