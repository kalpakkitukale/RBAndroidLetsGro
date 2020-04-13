package com.ramanbyte.aws_s3_android.utilities

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

import androidx.core.app.NotificationCompat

class S3NotificationUtils(private val mContext: Context) {

    // Clears notification tray messages
    fun clearNotifications() {
        if (notificationManager != null)
            notificationManager?.cancelAll()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun setupChannels() {
        val name = adminChannelName
        val description = adminChannelDescription
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(adminChannelId, name, importance)
        channel.description = description
        channel.setSound(null, null)
        channel.setShowBadge(true)
        channel.enableVibration(false)
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun removeNotificationById(notificationId: Int) {
        try {
            if (notificationManager != null)
                notificationManager?.cancel(notificationId)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updateUploadProgress(notificationId: Int, progress: Int, contentText: String) {
        if (mBuilder != null) {
            var message = "$progress%"
            if (progress >= 100) {
                message = "$contentText $progress%"
            }
            mBuilder!!
                .setContentText(message)
                .setOngoing(true)
                .setProgress(100, progress, false)

            notificationManager!!.notify(notificationId, mBuilder!!.build())
        }
    }

    fun showNotificationProgressOnFail(
        notificationId: Int,
        uploadIcon: Int,
        fileName: String,
        detailMessage: String
    ) {
        if (notificationManager == null) {
            notificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels()
        }

        if (mBuilder == null) {
            mBuilder = NotificationCompat.Builder(mContext, adminChannelId)
        }

        mBuilder!!
            .setSmallIcon(uploadIcon)
            .setContentTitle(fileName)
            .setOngoing(false)
            .setStyle(NotificationCompat.BigTextStyle().bigText(detailMessage))


        notificationManager!!.notify(notificationId, mBuilder!!.build())
    }

    fun showUploadProgress(
        notificationId: Int,
        uploadIcon: Int,
        fileName: String,
        progress: Int,
        detailMessage: String
    ) {
        if (notificationManager == null) {
            notificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels()
        }

        Log.d("S3Utils", "Progress --------$progress")

        if (mBuilder == null) {
            mBuilder = NotificationCompat.Builder(mContext, adminChannelId)
        }

        var indeterminate = true
        var isOnGoing = true

        if (progress > 0) {
            indeterminate = false
        }

        if (progress >= 100) {
            isOnGoing = false
        }


        mBuilder!!
            .setSmallIcon(uploadIcon)
            .setContentTitle(fileName)
            .setContentText("$progress%")
            .setOngoing(false)
            .setProgress(100, progress, indeterminate)
            .setStyle(NotificationCompat.BigTextStyle().bigText(detailMessage))


        notificationManager!!.notify(notificationId, mBuilder!!.build())
    }

    companion object {

        private val TAG = S3NotificationUtils::class.java.simpleName

        private val adminChannelId = "channelId"
        private val adminChannelName = "Notifications"
        private val adminChannelDescription = "No sound"

        @Volatile
        private var notificationManager: NotificationManager? = null
        @Volatile
        private var mBuilder: NotificationCompat.Builder? = null
    }
}
