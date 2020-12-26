package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var selectedOption: DownloadUrls? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId != -1) {
                when (radioGroup.checkedRadioButtonId) {
                    radioGlide.id -> {
                        selectedOption = DownloadUrls.GLIDE
                    }
                    radioUdacity.id -> {
                        selectedOption = DownloadUrls.UDACITY
                    }
                    radioRetrofit.id -> {
                        selectedOption = DownloadUrls.RETROFIT
                    }
                }
                download(selectedOption!!)
                custom_button.startLoading()
            } else
                Toast.makeText(this, getString(R.string.select_option_message), Toast.LENGTH_SHORT).show()
        }

        createChannel(
                getString(R.string.download_notification_channel_id),
                getString(R.string.notification_channel)
        )


    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = status == DownloadManager.STATUS_SUCCESSFUL
                    val downlaodTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    sendNotificaiton(isSuccess, downlaodTitle)
                }
                Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT).show()
                custom_button.setDelayedCompleted()
            }

        }
    }

    private fun sendNotificaiton(isSuccess: Boolean, downlaodTitle: String) {
        val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(this,
                isSuccess, downlaodTitle)
    }

    private fun download(downloadUrls: DownloadUrls) {
        val request =
                DownloadManager.Request(Uri.parse(downloadUrls.url))
                        .setTitle(getString(downloadUrls.title))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)
    }

}
