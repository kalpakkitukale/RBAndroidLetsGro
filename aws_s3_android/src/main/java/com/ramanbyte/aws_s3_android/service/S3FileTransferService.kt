package com.ramanbyte.aws_s3_android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.firebase.jobdispatcher.SimpleJobService
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.aws_s3_android.accessor.AppS3Client.Companion.createInstance
import org.json.JSONException
import java.io.File

class S3FileTransferService : SimpleJobService() {

    private val CLASS_TAG = S3FileTransferService::class.java.simpleName

    private var appS3Client: AppS3Client = createInstance(this)

    private var utils = Utils()

    override fun onRunJob(job: JobParameters?): Int {

        val jobTag = job?.tag

        val bundle = job?.extras

        if (bundle != null) {
            utils.accessKey = bundle.getString(AppS3Client.ACCESS_KEY)
            utils.secretKey = bundle.getString(AppS3Client.SECRET_KEY)

            val fileKey = bundle.getString(AppS3Client.FILE_KEY)
            val filePath = bundle.getString(AppS3Client.FILE_PATH)
            val notificationMessage = bundle.getString(AppS3Client.NOTIFICATION_MESSAGE)!!

            val transferUtility = utils.getTransferUtility(this)

            when (jobTag) {

                AppS3Client.TAG_DOWNLOAD -> {
                    val notificationDownloadId = bundle.getInt(AppS3Client.ID_DOWNLOAD_NOTIFICATION)
                    utils.removeNotification(notificationDownloadId)
                    utils.printLog("Download Started")
                    val transferObserver = transferUtility?.download(fileKey, File(filePath))
                    transferObserver?.setTransferListener(
                        fileKey?.let { utils.getOriginalFileName(it) }?.let {
                            DownloadListener(
                                notificationMessage,
                                it
                            )
                        }
                    )
                }

                /*AppS3Client.TAG_UPLOAD -> {
                    val notificationUploadId = bundle.getInt(AppS3Client.ID_UPLOAD_NOTIFICATION)
                    utils.removeNotification(notificationUploadId)
                    utils.printLog("Upload Started")
                    utils.printLog("Upload Started KEY ----- $fileKey")
                    utils.printLog("Upload Started PATH----- $filePath")
                    val transferObserver = transferUtility?.upload(fileKey, File(filePath))
                    transferObserver?.setTransferListener(UploadListener(notificationMessage, utils.getOriginalFileName(fileKey)))
                }

                AppS3Client.TAG_UPLOAD_1 -> {
                    val notificationUploadId = bundle.getInt(AppS3Client.ID_UPLOAD_NOTIFICATION)
                    utils.removeNotification(notificationUploadId)
                    utils.printLog("Upload Started For 1")
                    utils.printLog("Upload Started KEY ----- $fileKey")
                    utils.printLog("Upload Started PATH----- $filePath")
                    val transferObserver = transferUtility?.upload(fileKey, File(filePath))
                    transferObserver?.setTransferListener(UploadListener(notificationMessage, utils.getOriginalFileName(fileKey)))
                }*/
            }
        }

        return JobService.RESULT_SUCCESS
    }

    private inner class DownloadListener(
        val notificationMessage: String,
        val largeNotificationMessage: String
    ) : TransferListener {

        override fun onError(id: Int, e: Exception) {
            utils.printLog("Error in Downloading : To be Handled")
            e.printStackTrace()
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            val percentDonef = (bytesCurrent.toFloat() / bytesTotal.toFloat()) * 100
            val percentDone = percentDonef.toInt()
            utils.printLog("Bytes Downloaded: $bytesCurrent | Total Bytes: $bytesTotal | Percent: $percentDone% Downloaded")
//            utils.showNotificationForFile(appS3Client.notificationDownloadTitle, notificationMessage, id, largeNotificationMessage, false, percentDone, android.R.drawable.stat_sys_download)
        }

        override fun onStateChanged(id: Int, state: TransferState) {
            utils.printLog("Download TransferState ------ ${state.name}")
            when (state) {
                TransferState.COMPLETED -> {
//                    utils.showNotificationForFile("File Download", "File Downloaded SuccessFully", id, largeNotificationMessage, false, 100, android.R.drawable.stat_sys_download_done)
                    val intent = Intent("lp_attachment")
                    intent.putExtra("result", true)
                    sendBroadcast(intent)
                }
                TransferState.IN_PROGRESS -> {
                    utils.printLog("IN_PROGRESS")
                }

                TransferState.FAILED -> {
                    utils.printLog("FAILED")
                }

                TransferState.CANCELED -> {
                    utils.printLog("CANCELED")
                }

                TransferState.WAITING_FOR_NETWORK -> {
                    utils.printLog("WAITING_FOR_NETWORK")
                }

                TransferState.PAUSED -> {
                    utils.printLog("PAUSED")
                }

                TransferState.RESUMED_WAITING -> {
                    utils.printLog("RESUMED_WAITING")
                }
            }
        }
    }

    /*private inner class UploadListener(val notificationMessage: String, val largeNotificationMessage: String) : TransferListener {

        override fun onError(id: Int, e: Exception) {
            utils.printLog("Error in Uploading : To be Handled")
            e.printStackTrace()
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            //Show or Update Notification
            val percentDonef = (bytesCurrent.toFloat() / bytesTotal.toFloat()) * 100
            val percentDone = percentDonef.toInt()
            utils.printLog("Bytes Uploaded: $bytesCurrent | Total Bytes: $bytesTotal | Percent: $percentDone% Uploaded")
//            utils.showNotificationForFile(appS3Client.notificationUploadTitle, notificationMessage, id, largeNotificationMessage, false, percentDone, android.R.drawable.stat_sys_upload)
        }

        override fun onStateChanged(id: Int, state: TransferState) {
            //Update Notification
            utils.printLog("Upload TransferState ------ ${state.name}")
            when (state) {
                TransferState.COMPLETED -> {
//                    utils.showNotificationForFile("File Upload", "File Uploaded SuccessFully", id, largeNotificationMessage, false, 100, android.R.drawable.stat_sys_upload_done)
                }
                TransferState.IN_PROGRESS -> {
                    utils.printLog("IN_PROGRESS")
                }

                TransferState.FAILED -> {
                    utils.printLog("FAILED")
                }

                TransferState.CANCELED -> {
                    utils.printLog("CANCELED")
                }

                TransferState.WAITING_FOR_NETWORK -> {
                    utils.printLog("WAITING_FOR_NETWORK")
                }

                TransferState.PAUSED -> {
                    utils.printLog("PAUSED")
                }

                TransferState.RESUMED_WAITING -> {
                    utils.printLog("RESUMED_WAITING")
                }
            }
        }
    }*/

    private inner class Utils {

        private var sS3Client: AmazonS3Client? = null
        private lateinit var sMobileClient: AWSCredentialsProvider
        private var sTransferUtility: TransferUtility? = null

        var accessKey: String? = null
        var secretKey: String? = null

        fun getS3Client(): AmazonS3Client? {

            if (sS3Client == null) {

                // val awsCredentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)

                //sS3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1))

                /*awsCredentials = BasicAWSCredentials("AKIAQWQNOCWV3GIT4FKT", "TEz6bdNDgHAdzJp4zCI+/7hrnKewoxBAftaZleAl")*/

                sS3Client = AmazonS3Client(
                    AWSMobileClient.getInstance().awsCredentials,
                    Region.getRegion(Regions.AP_SOUTH_1)
                )

                try {
                    val regionString = AWSConfiguration(this@S3FileTransferService)
                        .optJsonObject("S3TransferUtility")
                        .getString("Region")
                    sS3Client?.setRegion(Region.getRegion(regionString))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return sS3Client
        }

        fun getTransferUtility(context: Context): TransferUtility? {
            if (sTransferUtility == null) {
                /* val cognitoCachingCredentialsProvider = CognitoCachingCredentialsProvider(applicationContext, AWSMobileClient.getInstance().configuration)
                 AmazonS3Client(AWSMobileClient.getInstance().awsCredentials, Region.getRegion(Regions.AP_SOUTH_1))*/
                sTransferUtility = TransferUtility.builder()
                    .context(context)
                    .s3Client(getS3Client())
                    //.defaultBucket(appS3Client.getDefaultBucket())
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .build()
            }

            return sTransferUtility
        }

        private val adminChannelId = "channelId"// "admin_channel"
        private val adminChannelName = "Notifications" //"Global Channel"
        private val adminChannelDescription = "No sound" //"Notifications sent from the app admin"
        private val KEY_NA = "NA"
        private val KEY_SUBMISSION_FILE_NAME_ADDITION_STRING = "_5_6_9"

        @RequiresApi(Build.VERSION_CODES.O)
        private fun setupChannels(notificationManager: NotificationManager) {

            val name = adminChannelName
            val description = adminChannelDescription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(adminChannelId, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel)

            /*val adminChannel: NotificationChannel = NotificationChannel(adminChannelId, adminChannelName, NotificationManager.IMPORTANCE_LOW).apply {
                description = adminChannelDescription
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(adminChannel)*/
        }

        fun removeNotification(notificationId: Int) {
            try {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*fun showNotificationForFile(
                titleMessage: String,
                notificationMessage: String,
                notificationId: Int,
                largeNotificationMessage: String,
                indeterminate: Boolean,
                progress: Int,
                notificationSmallIcon: Int
        ) {

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager)
            }

            var finalNotificationTitle = titleMessage
            var finalNotificationMessage = ""
            var onGoing = true

            if (progress < 100) {
                finalNotificationMessage = "$progress%"
                finalNotificationTitle = notificationMessage
            } else {
                onGoing = false
            }

            val mBuilder =
                    NotificationCompat.Builder(this@S3FileTransferService, adminChannelId).apply {
                        setSmallIcon(notificationSmallIcon)
                        setContentTitle(finalNotificationTitle)
                        setContentText(finalNotificationMessage)
                        setOngoing(onGoing)
                        setStyle(NotificationCompat.BigTextStyle().bigText(largeNotificationMessage))
                        setProgress(appS3Client.defaultMaxProgressForNotification, progress, indeterminate)
                    }

            notificationManager.notify(notificationId, mBuilder.build())
        }*/

        fun printLog(message: String) {
            Log.i(CLASS_TAG, message)
        }

        /**
         * @param ftpUniqueFileName
         * @return original file name with out unique ADDITIONAL "_5_6_9" String
         * @author Mansi Manakiki Mody
         * @since 19 July 2019     *
         */
        fun getOriginalFileName(ftpUniqueFileName: String): String {
            try {
                var uniqueFileName = ftpUniqueFileName
                if (ftpUniqueFileName != KEY_NA && ftpUniqueFileName.contains(
                        KEY_SUBMISSION_FILE_NAME_ADDITION_STRING
                    )
                ) {
                    val fileSplit = ftpUniqueFileName.split(Regex("/"))
                    if (fileSplit.isNotEmpty() && fileSplit.size > 0) {
                        uniqueFileName = fileSplit[1]
                    }
                    val fileExtension = uniqueFileName.substring(uniqueFileName.lastIndexOf("."))
                    val fileName = uniqueFileName.substring(
                        0,
                        uniqueFileName.lastIndexOf(KEY_SUBMISSION_FILE_NAME_ADDITION_STRING)
                    ).trim { it <= ' ' }
                    return fileName + fileExtension
                } else {
                    val fileSplit = ftpUniqueFileName.split(Regex("/"))
                    if (fileSplit.isNotEmpty() && fileSplit.size > 0) {
                        uniqueFileName = fileSplit[1]
                    }
                    return uniqueFileName
                }
            } catch (stringIndexOutOfBoundsException: StringIndexOutOfBoundsException) {
                stringIndexOutOfBoundsException.printStackTrace()
                return ftpUniqueFileName
            } catch (nullPointerException: NullPointerException) {
                nullPointerException.printStackTrace()
                return ftpUniqueFileName
            } catch (e: Exception) {
                e.printStackTrace()
                return ftpUniqueFileName
            }

        }
    }
}