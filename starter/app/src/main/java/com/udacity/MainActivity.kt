package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.w3c.dom.Text


class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var selectedUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            if (TextUtils.isEmpty(selectedUrl)) {
                Toast.makeText(this, getString(R.string.select_file_message), Toast.LENGTH_SHORT)
                    .show()
            } else
                download()
        }

        downloadGlide.setOnCheckedChangeListener(this)
        downloadLoadApp.setOnCheckedChangeListener(this)
        downloadRetrofit.setOnCheckedChangeListener(this)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.createChannel(
            this,
            getString(R.string.download_notification_channel_ID),
            getString(R.string.download_notification_channel_name)
        )

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val data = NotificationData(
                getString(R.string.notification_title),
                getString(R.string.notification_description),
                "",
                true
            )
            notificationManager.sendNotification(
                data, context
            )
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val glideURL =
            "https://codeload.github.com/bumptech/glide/zip/refs/heads/master"
        private const val loadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val retrofitURL =
            "https://codeload.github.com/square/retrofit/zip/refs/heads/master"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onCheckedChanged(button: CompoundButton, p1: Boolean) {
        when (button.id) {
            R.id.downloadGlide -> selectedUrl = glideURL
            R.id.downloadLoadApp -> selectedUrl = loadAppURL
            R.id.downloadRetrofit -> selectedUrl = retrofitURL
        }
    }

}
