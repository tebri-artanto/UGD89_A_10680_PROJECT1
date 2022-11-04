package com.example.ugd89_a_10680_project1

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.hardware.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity(), SensorEventListener{
    lateinit var sensorStatusTV: TextView
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    private var currentCameraId : Int = Camera.CameraInfo.CAMERA_FACING_BACK

    private val notificationId1 = 101
    private val CHANNEL_ID_1 = "channerl_notification_01"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorStatusTV = findViewById(R.id.idTVSensorStatus)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        createNotificationChannel()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Specify the sensor you want to listen to

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{ accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
        if (proximitySensor == null) {
            // on below line we are displaying a toast if no sensor is available
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // on below line we are registering
            // our sensor with sensor manager
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        try {
            mCamera = Camera.open()
        } catch (e: Exception) {
            Log.d("Error", "Failed to get Camera" + e.message)
        }

        if (mCamera != null) {
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{ view: View? -> System.exit(0) }


    }

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

//        override fun onSensorChanged(event: SensorEvent) {
//            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
//                if (event.values[0] == 0f) {
//                    sensorStatusTV.text = "<<<Near>>>"
//                    mCamera?.stopPreview()
//                    mCamera?.release()
//                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
//                    mCameraView = CameraView(this@MainActivity, mCamera!!)
//                    val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
//                    camera_view.addView(mCameraView)
//                } else {
//                    mCamera?.stopPreview()
//                    mCamera?.release()
//                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
//                    mCameraView = CameraView(this@MainActivity, mCamera!!)
//                    val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
//                    camera_view.addView(mCameraView)
//
//                    sensorStatusTV.text = "<<<<Away>>>>"
//                }
//            }
//        }
            override fun onSensorChanged(event: SensorEvent) {
                if (event.values[0] == 0f) {
                    if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        } else {
                            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                        }
                        if (mCameraView != null) {
                            mCamera?.stopPreview();
                        }
                        mCamera?.release();
                        try {
                            mCamera = Camera.open(currentCameraId)
                        } catch (e: Exception) {
                            Log.d("Error", "Failed to get Camera" + e.message)
                        }
                        if (mCamera != null) {
                            mCameraView = CameraView(applicationContext, mCamera!!)
                            val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
                            camera_view.addView(mCameraView)
                        }
                    }
                }
            }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")
            // Sides = Tilting phone left(10) and right(-10)
            val sides = event.values[0]
            // Up/Down = Tilting phone up(10), flat (0), upside-down(- 10)
            val upDown = event.values[1]

//            square.apply {
//                rotationX = upDown * 3f
//                rotationY = sides * 3f
//                rotation = -sides
//                translationX = sides * -10
//                translationY = upDown * 10
//
//            }
            // Changes the colour of the square if it's completely flat
            val color = if (upDown.toInt() == 0 && sides.toInt() == 0)
                Color.GREEN else Color.RED
            if (upDown.toInt()>3){
                sendNotification1()
            }
//            square.setBackgroundColor(color)
//            square.text = "up/down ${upDown.toInt()}\nleft/right${sides.toInt()}"

        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()

    }
    private fun sendNotification1() {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentText("Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 ")
            .setContentTitle("Modul89_A_10680_PROJECT2")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1,builder.build())
        }
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_1,name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }

            val notificationManager : NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)


        }
    }




}