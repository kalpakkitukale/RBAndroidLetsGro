package com.ramanbyte.aws_s3_android.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.aws_s3_android.utilities.S3Constant
import com.ramanbyte.aws_s3_android.utilities.S3NotificationUtils
import com.ramanbyte.aws_s3_android.utilities.S3Utils
import com.ramanbyte.aws_s3_android.utilities.UploadListener
import java.io.File


class UploadWorker(private val mContext: Context, workerParams: WorkerParameters) :
    Worker(mContext, workerParams) {

    override fun doWork(): Result {
        try {

            val s3Utils = S3Utils()

            s3Utils.printLog("doWork called")

            S3Constant().initUpload(mContext)

            val transferUtility = s3Utils.getTransferUtility(mContext)

            S3Utils.accessKey = inputData.getString(AppS3Client.ACCESS_KEY)
            S3Utils.secretKey = inputData.getString(AppS3Client.SECRET_KEY)

            val notificationMessage = inputData.getString(AppS3Client.NOTIFICATION_MESSAGE)
            val notificationUploadId = inputData.getInt(AppS3Client.ID_UPLOAD_NOTIFICATION, 0)
            S3NotificationUtils(mContext).removeNotificationById(notificationUploadId)

            if (!inputData.getBoolean(AppS3Client.FILE_MULTIPLE, false)) {

                val fileKey = inputData.getString(AppS3Client.FILE_KEY)
                val filePath = inputData.getString(AppS3Client.FILE_PATH)

                s3Utils.printLog("Uploading " + fileKey!!)

                val transferObserver: TransferObserver =
                    transferUtility!!.upload(fileKey, File(filePath))
                transferObserver.setTransferListener(
                    UploadListener(
                        notificationMessage!!,
                        s3Utils.getOriginalFileName(fileKey),
                        mContext
                    )
                )
                S3Constant.updateUploads(mContext)
            } else {
                s3Utils.printLog("list Key ------ " + inputData.getStringArray(AppS3Client.FILE_KEY))
                s3Utils.printLog("list PAth ------ " + inputData.getStringArray(AppS3Client.FILE_PATH))

                val fileKeys = inputData.getStringArray(AppS3Client.FILE_KEY)
                val filePaths = inputData.getStringArray(AppS3Client.FILE_PATH)

                for (i in fileKeys!!.indices) {
                    s3Utils.printLog("Key ------ " + fileKeys[i] as String)
                    s3Utils.printLog("PAth ------ " + filePaths!![i].toString())

                    s3Utils.printLog("Uploading " + fileKeys[i])

                    val transferObserver: TransferObserver =
                        transferUtility!!.upload(fileKeys[i], File(filePaths[i]))
                    transferObserver.setTransferListener(
                        UploadListener(
                            notificationMessage!!,
                            s3Utils.getOriginalFileName(fileKeys[i].toString()),
                            mContext
                        )
                    )
                    S3Constant.updateUploads(mContext)
                }

            }

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

    }


}
