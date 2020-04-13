package com.ramanbyte.aws_s3_android.utilities

import android.content.Context
import android.util.Log
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import org.json.JSONException

class S3Utils {

    private val CLASS_TAG = S3Utils::class.java.simpleName
    private var sS3Client: AmazonS3Client? = null
    private lateinit var sMobileClient: AWSCredentialsProvider
    private var sTransferUtility: TransferUtility? = null

    companion object {
        var accessKey: String? = null
        var secretKey: String? = null
    }

    fun getS3Client(context: Context): AmazonS3Client? {

        if (sS3Client == null) {

            // val awsCredentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)

            //sS3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1))

            AWSConfiguration(context).optJsonObject("CredDaKey").apply {
                accessKey = getString("KeyDaAccess")
                secretKey = getString("KeyDaSecret")
            }

            val awsCredentials = BasicAWSCredentials(accessKey, secretKey)

            //val cognitoCachingCredentialsProvider = CognitoCachingCredentialsProvider(context, "ap-south-1:1175e181-7760-45b6-8e59-8398a05c2f3e", Regions.AP_SOUTH_1)

            sS3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTH_1))

            try {
                val regionString = AWSConfiguration(context)
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
                .s3Client(getS3Client(context))
                .defaultBucket(AppS3Client.createInstance(context).getDefaultBucket())
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .build()
        }

        return sTransferUtility
    }

    private val KEY_NA = "NA"
    private val KEY_SUBMISSION_FILE_NAME_ADDITION_STRING = "_5_6_9"

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