package com.ramanbyte.aws_s3_android.service

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.aws_s3_android.utilities.S3Constant
import com.ramanbyte.aws_s3_android.utilities.S3NotificationUtils
import com.ramanbyte.aws_s3_android.utilities.S3Utils
import com.ramanbyte.aws_s3_android.utilities.UploadListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class UploadWorkerV2(private val mContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(mContext, workerParams), TransferListener {


    var fileName = ""

    var state: TransferState? = null
    var uploadProgress = 0

    var broadcastAction: String = ""

    override suspend fun doWork(): Result {
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

            val fileKey = inputData.getString(AppS3Client.FILE_KEY)
            val filePath = inputData.getString(AppS3Client.FILE_PATH)

            s3Utils.printLog("Uploading " + fileKey!!)

            broadcastAction = inputData.getString(AppS3Client.TAG_DOWNLOAD) ?: ""
            fileName = s3Utils.getOriginalFileName(fileKey)

            val transferObserver: TransferObserver =
                transferUtility!!.upload(fileKey, File(filePath))
            transferObserver.setTransferListener(this)
            S3Constant.updateUploads(mContext)

            /*if (!inputData.getBoolean(AppS3Client.FILE_MULTIPLE, false)) {


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

            }*/

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

        val percentDonef = (bytesCurrent.toFloat() / bytesTotal.toFloat()) * 100
        val progress = percentDonef.toInt()

        this.uploadProgress = progress

        mContext.sendBroadcast(Intent(broadcastAction).apply {

            putExtra(S3Constant.KeyProgress, progress)
            putExtra(S3Constant.KeyOpState, state?.name)
        })

       /* CoroutineScope(Dispatchers.Unconfined).launch {

            setProgress(
                workDataOf(
                    S3Constant.KeyProgress to progress,
                    S3Constant.KeyFileName to fileName
                )
            )
        }*/
    }

    override fun onStateChanged(id: Int, state: TransferState?) {

        this.state = state

        mContext.sendBroadcast(Intent(broadcastAction).apply {

            putExtra(S3Constant.KeyProgress, uploadProgress)
            putExtra(S3Constant.KeyOpState, state?.name)
        })

        /*CoroutineScope(Dispatchers.Unconfined).launch {

            setProgress(
                workDataOf(
                    S3Constant.KeyProgress to uploadProgress,
                    S3Constant.KeyFileName to fileName
                )
            )
        }*/
    }

    override fun onError(id: Int, ex: java.lang.Exception?) {

    }


}
