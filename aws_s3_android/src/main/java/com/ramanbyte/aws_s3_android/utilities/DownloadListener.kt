package com.ramanbyte.aws_s3_android.utilities

import android.R
import android.content.Context
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState

class DownloadListener(
    private val fileName: String,
    private val detailMessage: String,
    private val mContext: Context
) : TransferListener {

    private val s3Utils: S3Utils = S3Utils()

    // Simply updates the list when notified.
    override fun onError(id: Int, e: Exception) {
        s3Utils.printLog("onError: $id")
    }

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        val percentDonef = (bytesCurrent.toFloat() / bytesTotal.toFloat()) * 100
        val progress = percentDonef.toInt()
        //S3Constant.updateUploads(mContext)
        s3Utils.printLog(
            String.format(
                "onProgressChanged: %d, total: %d, current: %d",
                id, bytesTotal, bytesCurrent
            )
        )
        S3NotificationUtils(mContext).updateUploadProgress(id, progress, fileName)
    }

    override fun onStateChanged(id: Int, state: TransferState) {
        s3Utils.printLog("onStateChanged: $id, $state")
        //      S3Constant.updateUploads(insertinsertmContext)
        when (state) {
            TransferState.COMPLETED -> {
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_download_done,
                    fileName,
                    100,
                    detailMessage
                )
            }

            TransferState.IN_PROGRESS -> {
                s3Utils.printLog("IN_PROGRESS")
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_download,
                    fileName,
                    0,
                    detailMessage
                )
            }

            TransferState.WAITING_FOR_NETWORK -> {
                s3Utils.printLog("WAITING_FOR_NETWORK")
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_upload_done,
                    fileName,
                    0,
                    "WAITING_FOR_NETWORK"
                )
            }

            TransferState.FAILED -> {
                s3Utils.printLog("FAILED")
                S3NotificationUtils(mContext).showNotificationProgressOnFail(
                    id,
                    R.drawable.stat_sys_download_done,
                    fileName,
                    "File download failed"
                )
            }

            TransferState.PAUSED -> {
                s3Utils.printLog("PAUSED")
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_download,
                    fileName,
                    0,
                    "File download paused"
                )
            }

            TransferState.RESUMED_WAITING -> {
                s3Utils.printLog("RESUMED_WAITING")
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_download_done,
                    fileName,
                    0,
                    "File download waiting for resume"
                )
            }
            TransferState.WAITING -> {
                s3Utils.printLog("WAITING")
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_download,
                    fileName,
                    0,
                    "File download waiting for resume"
                )
            }
            TransferState.CANCELED -> {
                s3Utils.printLog("CANCELED")
                S3NotificationUtils(mContext).showUploadProgress(
                    id,
                    R.drawable.stat_sys_download_done,
                    fileName,
                    0,
                    "File download canceled"
                )
            }
            TransferState.PART_COMPLETED -> {
                s3Utils.printLog("PART_COMPLETED")
            }
            TransferState.PENDING_CANCEL -> {
                s3Utils.printLog("PENDING_CANCEL")
            }
            TransferState.PENDING_PAUSE -> {
                s3Utils.printLog("PENDING_PAUSE")
            }
            TransferState.PENDING_NETWORK_DISCONNECT -> {
                s3Utils.printLog("PENDING_NETWORK_DISCONNECT")
            }
            TransferState.UNKNOWN -> {
                s3Utils.printLog("UNKNOWN")
            }
        }

    }
}
