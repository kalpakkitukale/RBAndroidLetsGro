package com.ramanbyte.emla.content

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.ramanbyte.R
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import java.io.File
import java.io.IOException
import java.util.HashMap

class ExoMediaDownloadUtil {

    companion object {

        private var cache: Cache? = null
        private var downloadManager: DownloadManager? = null
        private var downloads: HashMap<Uri, Download>? = null
        private var downloadIndex: DownloadIndex? = null
        private var downloadCache: Cache? = null
        private var databaseProvider: DatabaseProvider? = null
        private var downloadDirectory: File? = null

        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

        @Synchronized
        fun loadDownloads(mContext: Context) {
            if (downloadManager == null) {
                getDownloadManager(mContext)
            }
            downloads = HashMap()
            downloadIndex = downloadManager!!.downloadIndex
            try {
                downloadIndex?.getDownloads().use { loadedDownloads ->
                    while (loadedDownloads!!.moveToNext()) {
                        val download =
                                loadedDownloads.download
                        downloads!![download.request.uri] = download
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                AppLog.warningLog("Failed to query downloads")
            }
        }

        @Synchronized
        fun removeDownloadedFile(uri: Uri, mContext: Context): Boolean {
            val download = downloads?.get(uri)
            if (download != null) {
                DownloadService.sendRemoveDownload(
                        mContext,
                        ExoDownloadService::class.java,
                        download.request.id,  /* foreground= */
                        false
                )
                return true
            }
            return false
        }

        @Synchronized
        fun getCache(mContext: Context): Cache {

            if (cache == null) {

                val databaseProvider = ExoDatabaseProvider(mContext)

                val cacheDirectory = File(mContext.getExternalFilesDir(null), "downloads")

                cache = SimpleCache(cacheDirectory, NoOpCacheEvictor(), databaseProvider)
            }

            return cache!!
        }

        @Synchronized
        fun getDownloadCache(mContext: Context): Cache? {
            if (downloadCache == null) {
                val downloadContentDirectory = File(
                        getDownloadDirectory(mContext),
                        DOWNLOAD_CONTENT_DIRECTORY
                )
                downloadCache =
                        SimpleCache(
                                downloadContentDirectory,
                                NoOpCacheEvictor(),
                                getDatabaseProvider(mContext)
                        )
            }
            return downloadCache
        }

        fun getDownloadDirectory(mContext: Context): File? {
            if (downloadDirectory == null) {
                downloadDirectory = mContext.getExternalFilesDir(null)
                if (downloadDirectory == null) {
                    downloadDirectory = mContext.filesDir
                }
            }
            return downloadDirectory
        }

        private fun getDatabaseProvider(mContext: Context): DatabaseProvider? {
            if (databaseProvider == null) {
                databaseProvider = ExoDatabaseProvider(mContext)
            }
            return databaseProvider
        }

        @Synchronized
        fun getDownloadManager(mContext: Context): DownloadManager {

            /*if (downloadManager == null) {

                val databaseProvider = ExoDatabaseProvider(mContext)

                val actionFile = File(mContext.externalCacheDir, "actions")

                downloadManager = DownloadManager(
                    mContext, databaseProvider, getCache(mContext), DefaultHttpDataSourceFactory(
                        Util.getUserAgent(
                            mContext,
                            BindingUtils.string(R.string.app_name)
                        )
                    )
                )
            }*/

            val downloadIndex = DefaultDownloadIndex(getDatabaseProvider(mContext))

            val downloaderConstructorHelper =
                    DownloaderConstructorHelper(
                            getDownloadCache(mContext),
                            DefaultHttpDataSourceFactory(
                                    Util.getUserAgent(
                                            mContext,
                                            BindingUtils.string(R.string.app_name)
                                    )
                            )
                    )

            downloadManager = DownloadManager(
                    mContext, downloadIndex, DefaultDownloaderFactory(downloaderConstructorHelper)
            )

            return downloadManager!!
        }

        class Notification(val mContext: Context) {

            private val adminChannelId = BindingUtils.string(R.string.notifications_channel_name)

            private val notificationId = 0x123456

            lateinit var notificationManager: NotificationManager

            fun build(
                    progress: Int
            ): android.app.Notification {

                notificationManager =
                        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setupChannels()
                }

                val mBuilder =
                        NotificationCompat.Builder(mContext, adminChannelId).apply {
                            setSmallIcon(R.mipmap.ic_launcher)
                            setContentTitle(
                                    "${BindingUtils.string(R.string.app_name)} - ${BindingUtils.string(
                                            R.string.str_content
                                    )}"
                            )
                            setContentText(BindingUtils.string(R.string.downloading_content))
                            setStyle(NotificationCompat.BigTextStyle().bigText(BindingUtils.string(R.string.downloading_content)))
                            setAutoCancel(true)
                            setOngoing(true)
                            setProgress(100, progress, false)
                        }

                return mBuilder.build()

                //notificationManager?.notify(notificationId, mBuilder.build())
            }

            fun cancelNotification(notificationId: Int) {
                if (notificationManager == null)
                    notificationManager =
                            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager!!.cancel(notificationId)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            private fun setupChannels() {
                val adminChannelName =
                        BindingUtils.string(R.string.exo_download_notification_channel_name)
                val adminChannelDescription =
                        BindingUtils.string(R.string.notifications_admin_channel_description)

                val adminChannel: NotificationChannel
                adminChannel = NotificationChannel(
                        adminChannelId,
                        adminChannelName,
                        NotificationManager.IMPORTANCE_LOW
                )
                adminChannel.description = adminChannelDescription
                adminChannel.enableLights(true)
                adminChannel.lightColor = Color.RED
                adminChannel.enableVibration(true)
                if (notificationManager != null) {
                    notificationManager!!.createNotificationChannel(adminChannel)
                }
            }
        }
    }
}