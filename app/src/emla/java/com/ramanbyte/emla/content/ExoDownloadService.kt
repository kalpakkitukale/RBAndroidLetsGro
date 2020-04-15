package com.ramanbyte.emla.content

import android.app.Notification
import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.DI_ACTIVITY_CONTEXT
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import org.kodein.di.generic.singleton

class ExoDownloadService : DownloadService, KodeinAware {

    private val parentKodein by kodein(this@ExoDownloadService)

    override val kodein by Kodein.lazy(allowSilentOverride = true) {
        extend(
            this@ExoDownloadService.parentKodein,
            true,
            Copy.All
        )
        bind<Context>(DI_ACTIVITY_CONTEXT) with singleton { this@ExoDownloadService }
        import(authModuleDependency, true)
    }

    val downloads: HashMap<Uri, Download> = HashMap()

    private val courseContentRepository: ContentRepository by instance()

    companion object {
        private val CHANNEL_ID = "download_channel"
        private val JOB_ID = 1
        private val FOREGROUND_NOTIFICATION_ID = 1

        private var nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1
    }


    private var notificationHelper: DownloadNotificationHelper? = null

    constructor() : super(
        FOREGROUND_NOTIFICATION_ID,
        DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
        CHANNEL_ID,
        com.google.android.exoplayer2.R.string.exo_download_notification_channel_name
    ) {

        nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = DownloadNotificationHelper(this, CHANNEL_ID)
    }

    override fun getDownloadManager(): DownloadManager {
        return ExoMediaDownloadUtil.getDownloadManager(this@ExoDownloadService)
    }

    override fun getScheduler(): PlatformScheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(downloads: List<Download>): Notification {
        return notificationHelper!!.buildProgressNotification(
            android.R.drawable.stat_sys_download, /* message= */ null, null, downloads
        )/* contentIntent= */
    }

    override fun onDownloadChanged(download: Download) {

        AppLog.infoLog("download_request_id ${download.request.id}")

        courseContentRepository.updateMediaInfoByDownloadId(
            download.state,
            download.request.id
        )

        downloads.put(download.request.uri, download)
        AppLog.infoLog("download_request_uri ${download.request.uri}")

        val notification: Notification = when {

            download.state == Download.STATE_COMPLETED -> {

                /* contentRepository.updateMediaInfoByDownloadId(
                     Download.STATE_COMPLETED,
                     download.request.id
                 )*/
                AppLog.infoLog("onDownloadChanged :: State :: ${download.state}")
                notificationHelper!!.buildDownloadCompletedNotification(
                    android.R.drawable.stat_sys_download_done,
                    /* contentIntent= */ null,
                    Util.fromUtf8Bytes(download.request.data)
                )
            }/* contentIntent= */
            download.state == Download.STATE_FAILED -> {

                /*contentRepository.updateMediaInfoByDownloadId(
                    Download.STATE_FAILED,
                    download.request.id
                )*/
                AppLog.infoLog("onDownloadChanged :: State :: ${download.state}")
                notificationHelper!!.buildDownloadFailedNotification(
                    android.R.drawable.stat_sys_download_done, null,
                    Util.fromUtf8Bytes(download.request.data)
                )
            }

            else -> {
                AppLog.infoLog("onDownloadChanged :: State :: ${download.state}")

                return
            }
        }
        NotificationUtil.setNotification(this, nextNotificationId++, notification)
    }

    override fun onDownloadRemoved(download: Download?) {

        downloads.remove(download?.request?.uri)

        /*val notification: Notification = when {
            download?.state == Download.STATE_REMOVING -> {

                AppLog.infoLog("onDownloadRemoved :: State :: ${download.state}")
            *//*    notificationHelper!!.buildDownloadFailedNotification(
                    android.R.drawable.stat_sys_download_done, null,
                    Util.fromUtf8Bytes(download.request.data)
                )*//*

            }
            else -> {
                AppLog.infoLog("onDownloadRemoved :: State :: ${download?.state}")
               *//* notificationHelper!!.buildDownloadFailedNotification(
                    android.R.drawable.stat_sys_download_done, null,
                    Util.fromUtf8Bytes(download?.request?.data)
                )*//*
                return
            }
        }
        NotificationUtil.setNotification(this, nextNotificationId++, notification)*/

        super.onDownloadRemoved(download)
    }


}