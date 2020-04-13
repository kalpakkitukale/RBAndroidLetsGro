package com.ramanbyte.aws_s3_android.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.aws_s3_android.utilities.DownloadListener
import com.ramanbyte.aws_s3_android.utilities.S3Constant
import com.ramanbyte.aws_s3_android.utilities.S3NotificationUtils
import com.ramanbyte.aws_s3_android.utilities.S3Utils
import java.io.File

class DownloadWorker(private val mContext: Context, workerParams: WorkerParameters) :
    Worker(mContext, workerParams) {

    override fun doWork(): Result {
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

            val transferObserver: TransferObserver

            s3Utils.printLog("Downloading " + fileKey!!)
            transferObserver = transferUtility!!.download(fileKey, File(filePath))
            transferObserver.setTransferListener(
                DownloadListener(
                    notificationMessage!!,
                    s3Utils.getOriginalFileName(fileKey),
                    mContext
                )
            )
            S3Constant.updateDownloads(mContext)

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

    }
}
