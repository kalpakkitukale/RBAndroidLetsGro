package com.ramanbyte.aws_s3_android.utilities

import android.annotation.SuppressLint
import android.content.Context
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import java.util.*

class S3Constant {

    private val CLASS_TAG = S3Constant::class.java.simpleName

    fun initUpload(context: Context) {
        mContext = context
        uploadTransferUtility = S3Utils().getTransferUtility(context)
        if (uploadTransferRecordMaps == null) {
            uploadTransferRecordMaps = ArrayList<HashMap<String, Any>>()
        }
    }

    fun initDownload(context: Context) {
        mContext = context
        downloadTransferUtility = S3Utils().getTransferUtility(context)
        if (downloadTransferRecordMaps == null) {
            downloadTransferRecordMaps = ArrayList<HashMap<String, Any>>()
        }
    }

    companion object {

        /*
        * Constants Keys
        * */

        const val KeyProgress = "key_progress"
        const val KeyFileName = "key_file_name"
        const val KeyOpState = "key_op_state"

        /*
        * Constants Keys Ends
        * */

        // The TransferUtility is the primary class for managing transfer to S3
        var uploadTransferUtility: TransferUtility? = null
        var downloadTransferUtility: TransferUtility? = null
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null

        // A List of all transfers
        var uploadObservers: List<TransferObserver>? = null
        var downloadObservers: List<TransferObserver>? = null

        /**
         * This map is used to provide data to the SimpleAdapter above. See the
         * fillMap() function for how it relates observers to rows in the displayed
         * activity.
         */
        internal var uploadTransferRecordMaps: ArrayList<HashMap<String, Any>>? = null
        internal var downloadTransferRecordMaps: ArrayList<HashMap<String, Any>>? = null

        /**
         * Gets all relevant transfers from the Transfer Service for populating the
         * UI
         */
        fun updateDownloads(context: Context) {
            downloadTransferRecordMaps?.clear()
            // Use TransferUtility to get all upload transfers.
            downloadObservers =
                S3Utils().getTransferUtility(context)?.getTransfersWithType(TransferType.DOWNLOAD)
            val listener = DownloadListener("", "", mContext!!)
            for (observer in this.downloadObservers!!) {
                observer.refresh()

                // Sets listeners to in progress transfers
                if (TransferState.WAITING == observer.state
                    || TransferState.WAITING_FOR_NETWORK == observer.state
                    || TransferState.IN_PROGRESS == observer.state
                ) {
                    observer.setTransferListener(listener)
                }

                //  S3Utils().printLog("Transfer Id:  ${observer.id} | Transfer State: ${observer.state.name} | %age Uploaded: ${(observer.bytesTransferred.toFloat() / observer.bytesTotal.toFloat())*100}")

                /* if (observer.state == TransferState.COMPLETED)
                     S3NotificationUtils.showUploadProgress(context, observer.id, android.R.drawable.stat_sys_upload_done, "", 100, observer.state.name)
                 else
                    S3NotificationUtils.updateUploadProgress(context, observer.id, ((observer.bytesTransferred.toFloat() / observer.bytesTotal.toFloat()) * 100).toInt(), observer.state.name)*/


            }

        }


        fun updateUploads(context: Context) {
            uploadTransferRecordMaps?.clear()
            // Use TransferUtility to get all upload transfers.
            uploadObservers =
                S3Utils().getTransferUtility(context)?.getTransfersWithType(TransferType.UPLOAD)
            val listener = UploadListener("", "", mContext!!)
            for (observer in this.uploadObservers!!) {
                observer.refresh()

                // Sets listeners to in progress transfers
                if (TransferState.WAITING == observer.state
                    || TransferState.WAITING_FOR_NETWORK == observer.state
                    || TransferState.IN_PROGRESS == observer.state
                ) {
                    observer.setTransferListener(listener)
                }

                //  S3Utils().printLog("Transfer Id:  ${observer.id} | Transfer State: ${observer.state.name} | %age Uploaded: ${(observer.bytesTransferred.toFloat() / observer.bytesTotal.toFloat())*100}")

                /* if (observer.state == TransferState.COMPLETED)
                     S3NotificationUtils.showUploadProgress(context, observer.id, android.R.drawable.stat_sys_upload_done, "", 100, observer.state.name)
                 else
                    S3NotificationUtils.updateUploadProgress(context, observer.id, ((observer.bytesTransferred.toFloat() / observer.bytesTotal.toFloat()) * 100).toInt(), observer.state.name)*/


            }

        }
    }
}
