package com.ramanbyte.aws_s3_android.service

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.aws_s3_android.utilities.S3Constant
import com.ramanbyte.aws_s3_android.utilities.S3Constant.Companion.KeyOpState
import com.ramanbyte.aws_s3_android.utilities.S3Constant.Companion.KeyProgress
import com.ramanbyte.aws_s3_android.utilities.S3NotificationUtils
import com.ramanbyte.aws_s3_android.utilities.S3Utils
import java.io.File

class DownloadWorkerV2(private val mContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(mContext, workerParams), TransferListener {

    var fileName = ""

    var state: TransferState? = null
    var downloadProgress = 0

    var broadcastAction: String = ""

    override suspend fun doWork(): Result {

        try {


            val s3Utils = S3Utils()

            s3Utils.printLog("doWork called")

            S3Constant().initDownload(mContext)

            val transferUtility = s3Utils.getTransferUtility(mContext)

            s3Utils.printLog("doWork called for File Download")
            S3Utils.accessKey = inputData.getString(AppS3Client.ACCESS_KEY)
            S3Utils.secretKey = inputData.getString(AppS3Client.SECRET_KEY)
            val fileKey = inputData.getString(AppS3Client.FILE_KEY)
            val filePath = inputData.getString(AppS3Client.FILE_PATH)
            val notificationMessage = inputData.getString(AppS3Client.NOTIFICATION_MESSAGE)
            val notificationUploadId = inputData.getInt(AppS3Client.ID_DOWNLOAD_NOTIFICATION, 0)
            S3NotificationUtils(mContext).removeNotificationById(notificationUploadId)

            broadcastAction = inputData.getString(AppS3Client.TAG_DOWNLOAD) ?: ""
            fileName = s3Utils.getOriginalFileName(fileKey!!)

            val transferObserver: TransferObserver

            s3Utils.printLog("Downloading " + fileKey!!)
            transferObserver = transferUtility!!.download(fileKey, File(filePath))

            transferObserver.setTransferListener(this)
            S3Constant.updateDownloads(mContext)

            return Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

    }

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

        val percentDonef = (bytesCurrent.toFloat() / bytesTotal.toFloat()) * 100
        val progress = percentDonef.toInt()

        this.downloadProgress = progress

        mContext.sendBroadcast(Intent(broadcastAction).apply {

            putExtra(KeyProgress, progress)
            putExtra(KeyOpState, state?.name)
        })

/*        CoroutineScope(Dispatchers.Unconfined).launch {

            setProgress(
                workDataOf(
                    KeyProgress to progress,
                    KeyFileName to fileName
                )
            )
        }*/

    }

    override fun onStateChanged(id: Int, state: TransferState?) {

        this.state = state

        mContext.sendBroadcast(Intent(broadcastAction).apply {

            putExtra(KeyProgress, downloadProgress)
            putExtra(KeyOpState, state?.name)
        })

        /*CoroutineScope(Dispatchers.Unconfined).launch {

            setProgress(
                workDataOf(
                    KeyProgress to downloadProgress,
                    KeyFileName to fileName
                )
            )
        }*/

    }

    override fun onError(id: Int, ex: java.lang.Exception?) {

    }
}
