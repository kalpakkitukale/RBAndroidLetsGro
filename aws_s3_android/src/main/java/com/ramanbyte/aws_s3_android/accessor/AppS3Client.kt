package com.ramanbyte.aws_s3_android.accessor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.work.*
import com.amazonaws.HttpMethod
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.firebase.jobdispatcher.*
import com.ramanbyte.aws_s3_android.service.*
import com.ramanbyte.aws_s3_android.utilities.S3Constant
import com.ramanbyte.aws_s3_android.utilities.S3NotificationUtils
import com.ramanbyte.aws_s3_android.utilities.S3Utils
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


class AppS3Client//AppLog.errorLog("Initialization error.", e);
private constructor(var applicationContext: Context) {

    private var secretKey: String? = null

    private var accessKey: String? = null

    private var s3Buckets: HashMap<Long, String>? = null

    private var defaultBucketKey = 0x8275218992

    private var s3Operation: S3Operations? = null

    var defaultMaxProgressForNotification = 100
    var applicationIconForNotification = 0
    var subObject = ""

    var notificationDownloadTitle = "Downloading File(s)"
    var notificationUploadTitle = "Uploading File(s)"
    private var transferRecordMaps: ArrayList<java.util.HashMap<String, Any>>? = null

    init {
        s3Buckets = HashMap()
        s3Operation = S3Operations()

        try {
            addBucket(
                0X1, AWSConfiguration(applicationContext)
                    .optJsonObject("S3TransferUtility")
                    .getString("Bucket"), true
            )

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        /* AWSMobileClient.getInstance().apply {
             initialize(
                     applicationContext,
                     AWSConfiguration(applicationContext, R.raw.awsconfiguration),
                     object : Callback<UserStateDetails> {

                         override fun onResult(userStateDetails: UserStateDetails) {
                             Log.i("AWS Client", "AWSMobileClient initialized. User State is " + userStateDetails.userState)
                         }

                         override fun onError(e: Exception) {
                             Log.i("AWS Client", "AWS Init Error")
                             e.printStackTrace()
                             //AppLog.errorLog("Initialization error.", e);
                         }
                     }
             )

         }
         */
        TransferNetworkLossHandler.getInstance(applicationContext)
    }

    companion object {

        const val ACCESS_KEY = "access_key"
        const val SECRET_KEY = "secret_key"
        const val FILE_KEY = "file_key"
        const val FILE_MULTIPLE = "file_multiple"
        const val FILE_PATH = "file_path"
        const val TAG_UPLOAD = "tag_upload"
        const val TAG_UPLOAD_1 = "tag_upload_1"
        const val TAG_DOWNLOAD = "tag_download"
        const val ID_UPLOAD_NOTIFICATION = "id_upload"
        const val ID_DOWNLOAD_NOTIFICATION = "id_download"
        const val NOTIFICATION_MESSAGE = "notification_message"

        const val adminChannelId = "channelId"// "admin_channel"
        const val adminChannelName = "Notifications" //"Global Channel"
        const val adminChannelDescription = "No sound" //"Notifications sent from the app admin"

        private var appS3Client: AppS3Client? = null

        fun createInstance(applicationContext: Context): AppS3Client {
            if (appS3Client == null) {
                appS3Client = AppS3Client(applicationContext)

                applicationContext.startService(
                    Intent(
                        applicationContext,
                        TransferService::class.java
                    )
                )
            }

            return appS3Client!!
        }
    }

    fun addBucket(bucketKey: Long, bucketName: String, setAsDefault: Boolean) {
        s3Buckets?.put(bucketKey, bucketName)

        if (setAsDefault) {
            defaultBucketKey = bucketKey
        }
    }

    fun setDefaultBucket(bucketKey: Long) {
        defaultBucketKey = bucketKey
    }

    fun getDefaultBucket(): String {
        return s3Buckets?.get(defaultBucketKey)!!
    }

    fun setSecretKey(secretKey: String) {
        this.secretKey = secretKey
    }

    fun setAccessKey(accessKey: String) {
        this.accessKey = accessKey
    }

    fun setDefaultObject(subObject: String) {
        this.subObject = subObject
    }

    fun getDefaultObject(): String {
        return this.subObject
    }

    fun setAWSRegion() {

    }

    fun download(
        fileKey: String,
        filePath: String,
        notificationMessage: String,
        notificationId: Int
    ) {
        S3NotificationUtils(applicationContext).showUploadProgress(
            notificationId,
            android.R.drawable.stat_notify_sync,
            "Preparing files for download...",
            0,
            ""
        )
        /*Thread(Runnable {
            S3Constant().initDownload(applicationContext)
            s3Operation?.download(fileKey, filePath, notificationMessage, notificationId)
        }).start()*/

        val data = Data.Builder()
            .putString(SECRET_KEY, secretKey)
            .putString(ACCESS_KEY, accessKey)
            .putString(FILE_KEY, "${getDefaultObject()}/$fileKey")
            .putString(NOTIFICATION_MESSAGE, notificationMessage)
            .putString(FILE_PATH, filePath)
            .putInt(ID_DOWNLOAD_NOTIFICATION, notificationId)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val simpleRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(fileKey)
            .build()

        WorkManager.getInstance().enqueue(simpleRequest)
    }

    fun download(
        fileKey: String,
        filePath: String,
        intentAction: String
    ): LiveData<AppS3FileInfo> {

        val data = Data.Builder()
            .putString(SECRET_KEY, secretKey)
            .putString(ACCESS_KEY, accessKey)
            .putString(FILE_KEY, "${getDefaultObject()}/$fileKey")
            .putString(FILE_PATH, filePath)
            .putString(TAG_DOWNLOAD, intentAction)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val simpleRequest = OneTimeWorkRequest.Builder(DownloadWorkerV2::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(fileKey)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(simpleRequest)

        return Transformations.switchMap(
            WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(
                simpleRequest.id
            )
        ) { workInfo ->

            MutableLiveData<AppS3FileInfo>().apply {

                AppS3FileInfo(AppS3FileInfo.Download).apply {

                    //infoId = simpleRequest.id
                    progress = workInfo.progress.getInt(S3Constant.KeyProgress, 0)
                    fileName = workInfo.progress.getString(S3Constant.KeyFileName) ?: ""
                    status = workInfo.state.name
                    isFinished = workInfo.state.isFinished
                }

            }
        }
    }

    fun upload(
        fileKey: String,
        filePath: String,
        intentAction: String
    ): LiveData<AppS3FileInfo> {

        S3Utils().printLog("File Key ------ $fileKey")
        S3Utils().printLog("File Path ------ $filePath")

        val data = Data.Builder()
            .putString(SECRET_KEY, secretKey)
            .putString(ACCESS_KEY, accessKey)
            .putString(FILE_KEY, "${getDefaultObject()}/$fileKey")
            .putBoolean(FILE_MULTIPLE, false)
            .putString(FILE_PATH, filePath)
            .putString(TAG_DOWNLOAD, intentAction)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val simpleRequest = OneTimeWorkRequest.Builder(UploadWorkerV2::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(fileKey)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(simpleRequest)

        return Transformations.switchMap(
            WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(
                simpleRequest.id
            )
        ) { workInfo ->

            MutableLiveData<AppS3FileInfo>().apply {

                AppS3FileInfo(AppS3FileInfo.Upload).apply {

                    //infoId = simpleRequest.id
                    progress = workInfo.progress.getInt(S3Constant.KeyProgress, 0)
                    fileName = workInfo.progress.getString(S3Constant.KeyFileName) ?: ""
                    status = workInfo.state.name
                    isFinished = workInfo.state.isFinished
                }

            }
        }
    }

    fun uploadMultiple(
        fileKeys: Array<String?>,
        filePaths: Array<String?>,
        notificationMessage: String
    ) {
        S3NotificationUtils(applicationContext).showUploadProgress(
            0X100,
            android.R.drawable.stat_sys_upload,
            "Preparing files for upload...",
            0,
            ""
        )

        val defaultObjectFileKeys = fileKeys.map { fileKey ->
            "${getDefaultObject()}/$fileKey"
        }.toTypedArray()

        val data = Data.Builder()
            .putString(SECRET_KEY, secretKey)
            .putString(ACCESS_KEY, accessKey)
            .putStringArray(FILE_KEY, defaultObjectFileKeys)
            .putBoolean(FILE_MULTIPLE, true)
            .putString(NOTIFICATION_MESSAGE, notificationMessage)
            .putStringArray(FILE_PATH, filePaths)
            .putInt(ID_UPLOAD_NOTIFICATION, 0X100)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val simpleRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance().enqueue(simpleRequest)


        /*Thread(Runnable {
            S3Constant().initUpload(applicationContext)
            s3Operation?.upload(fileKey, filePath, notificationMessage, tag, notificationId)
        }).start()*/
    }

    fun upload(
        fileKey: String,
        filePath: String,
        notificationMessage: String,
        tag: String,
        notificationId: Int
    ) {

        S3Utils().printLog("File Key ------ $fileKey")
        S3Utils().printLog("File Path ------ $filePath")

        S3NotificationUtils(applicationContext).showUploadProgress(
            notificationId,
            android.R.drawable.stat_sys_upload,
            "Preparing files for upload...",
            0,
            ""
        )

        val data = Data.Builder()
            .putString(SECRET_KEY, secretKey)
            .putString(ACCESS_KEY, accessKey)
            .putString(FILE_KEY, "${getDefaultObject()}/$fileKey")
            .putBoolean(FILE_MULTIPLE, false)
            .putString(NOTIFICATION_MESSAGE, notificationMessage)
            .putString(FILE_PATH, filePath)
            .putInt(ID_UPLOAD_NOTIFICATION, notificationId)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val simpleRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(fileKey)
            .build()

        val ops = WorkManager.getInstance().enqueue(simpleRequest)


        /*Thread(Runnable {
            S3Constant().initUpload(applicationContext)
            s3Operation?.upload(fileKey, filePath, notificationMessage, tag, notificationId)
        }).start()*/
    }

    fun getFileAccessUrl(fileName: String): String? {
        return s3Operation?.getPresignedUrl(fileName)
    }

    private inner class S3Operations {

        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))

        fun upload(
            fileKey: String,
            filePath: String,
            notificationMessage: String,
            tag: String,
            notificationId: Int
        ) {
//            showNotificationForFile("Preparing files for upload...", notificationId, android.R.drawable.stat_sys_upload)
            //Schedule Service
            /*val bundle = Bundle().apply {
                putString(SECRET_KEY, secretKey)
                putString(ACCESS_KEY, accessKey)
                putString(FILE_KEY, fileKey)
                putString(NOTIFICATION_MESSAGE, notificationMessage)
                putString(FILE_PATH, filePath)
                putInt(ID_UPLOAD_NOTIFICATION, notificationId)
            }

            scheduleFileTransferJob(tag, bundle)*/


            val data = Data.Builder()
                .putString(SECRET_KEY, secretKey)
                .putString(ACCESS_KEY, accessKey)
                .putString(FILE_KEY, fileKey)
                .putString(NOTIFICATION_MESSAGE, notificationMessage)
                .putString(FILE_PATH, filePath)
                .putInt(ID_UPLOAD_NOTIFICATION, notificationId)
                .build()

            /*

            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()

            val work = PeriodicWorkRequest.Builder(UploadWorker::class.java!!, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build()
            WorkManager.getInstance()
                    .enqueueUniquePeriodicWork("jobTag", ExistingPeriodicWorkPolicy.KEEP, work);*/

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()


            val simpleRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(fileKey)
                .build()

            WorkManager.getInstance().enqueue(simpleRequest)
        }

        fun download(
            fileKey: String,
            filePath: String,
            notificationMessage: String,
            notificationId: Int
        ) {
            //Schedule Service
//            showNotificationForFile("Preparing files for download...", notificationId, android.R.drawable.stat_sys_download)
            /*val bundle = Bundle().apply {
                putString(SECRET_KEY, secretKey)
                putString(ACCESS_KEY, accessKey)
                putString(FILE_KEY, fileKey)
                putString(NOTIFICATION_MESSAGE, notificationMessage)
                putString(FILE_PATH, filePath)
                putInt(ID_DOWNLOAD_NOTIFICATION, notificationId)
            }

            scheduleFileTransferJob(TAG_DOWNLOAD, bundle)*/

            val data = Data.Builder()
                .putString(SECRET_KEY, secretKey)
                .putString(ACCESS_KEY, accessKey)
                .putString(FILE_KEY, fileKey)
                .putString(NOTIFICATION_MESSAGE, notificationMessage)
                .putString(FILE_PATH, filePath)
                .putInt(ID_DOWNLOAD_NOTIFICATION, notificationId)
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()


            val simpleRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(fileKey)
                .build()

            WorkManager.getInstance().enqueue(simpleRequest)
        }

        private fun scheduleFileTransferJob(taskTag: String, dataBundle: Bundle) {

            val myJob = dispatcher.newJobBuilder()
                .setService(S3FileTransferService::class.java)
                .setTag(taskTag)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(0, 5))
                .setReplaceCurrent(false)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setExtras(dataBundle)
                .build()

            dispatcher.mustSchedule(myJob)
        }

        fun getS3Client(): AmazonS3Client? {

            var sS3Client: AmazonS3Client? = null

            if (sS3Client == null) {

                /*
                * Temp Commented
                * */
//                val awsCredentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
                val awsCredentials = AWSMobileClient.getInstance().awsCredentials
                sS3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1))

                /*awsCredentials = BasicAWSCredentials("AKIAQWQNOCWV3GIT4FKT", "TEz6bdNDgHAdzJp4zCI+/7hrnKewoxBAftaZleAl")

                try {
                    val regionString = AWSConfiguration(context)
                            .optJsonObject("S3TransferUtility")
                            .getString("Region")
                    sS3Client.setRegion(Region.getRegion(regionString))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }*/

            }
            return sS3Client
        }

        fun getPresignedUrl(fileKey: String): String {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val expirationTime = Calendar.getInstance().timeInMillis + (1000 * 60 * 60)

            val presignedUrlRequest = /*GeneratePresignedUrlRequest(getDefaultBucket(), fileKey).apply {
                withMethod(HttpMethod.GET)
                withExpiration(Date(expirationTime))
                public/example-image.png
            }*/

                GeneratePresignedUrlRequest(
                    getDefaultBucket(),
                    "${getDefaultObject()}/$fileKey"
                ).apply {
                    withMethod(HttpMethod.GET)
                    withExpiration(Date(expirationTime))
                }

            //val isExist = S3Utils().getS3Client(applicationContext)?.doesObjectExist(getDefaultBucket(), fileKey)

            return S3Utils().getS3Client(applicationContext)
                ?.generatePresignedUrl(presignedUrlRequest).toString()
            //return AmazonS3Client(AWSMobileClient.getInstance().awsCredentials, Region.getRegion(Regions.AP_SOUTH_1)).generatePresignedUrl(presignedUrlRequest).toString()

//            return getS3Client()?.generatePresignedUrl(presignedUrlRequest).toString()
        }


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
        }

        /*fun showNotificationForFile(
                titleMessage: String,
                notificationId: Int,
                notificationSmallIcon: Int
        ) {

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager)
            }

            val mBuilder =
                    NotificationCompat.Builder(applicationContext, adminChannelId).apply {
                        setSmallIcon(notificationSmallIcon)
                        setContentTitle(titleMessage)
                        setContentText("")
                        setOngoing(true)
                        setProgress(defaultMaxProgressForNotification, 0, true)
                    }

            notificationManager.notify(notificationId, mBuilder.build())
        }*/
    }

}