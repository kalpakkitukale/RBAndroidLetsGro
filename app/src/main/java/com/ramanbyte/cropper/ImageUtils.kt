package com.ramanbyte.cropper

import android.content.ContentUris
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.ramanbyte.BaseAppController
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.FileUtils
import java.io.*


/**
 * @author Shital Kadaganchi <shital.k@ramanbyte.com>
 * @since 12/10/19
 */

class ImageUtils {

    companion object {


        private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        fun decodeSampledBitmapFromPath(
            imagePath: String,
            reqWidth: Int, reqHeight: Int
        ): Bitmap {

            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(imagePath, options)
        }

        fun getDataColumn(
            context: Context,
            uri: Uri,
            selection: String,
            selectionArgs: Array<String>
        ): String? {

            val column = "_data"
            val projection = arrayOf(column)
            try {
                context.contentResolver.query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )!!.use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index = cursor.getColumnIndexOrThrow(column)
                        val value = cursor.getString(column_index)
                        return if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith(
                                "file://"
                            )
                        ) {
                            null
                        } else value
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }

            return null
        }

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        fun getRealPathFromURI(uri: Uri): String? {
            try {
                val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                if (isKitKat && DocumentsContract.isDocumentUri(
                        BaseAppController.applicationInstance,
                        uri
                    )
                ) {
                    if (isExternalStorageDocument(uri)) {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split =
                            docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        }
                    } else if (isDownloadsDocument(uri)) {
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        return BaseAppController.applicationInstance?.let {
                            getDataColumn(
                                it,
                                contentUri,
                                "",
                                emptyArray()
                            )
                        }
                    } else if (isMediaDocument(uri)) {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split =
                            docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]

                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }

                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])

                        return BaseAppController.applicationInstance?.let {
                            getDataColumn(
                                it,
                                contentUri!!,
                                selection,
                                selectionArgs
                            )
                        }
                    }
                } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

                    val filePath = BaseAppController.applicationInstance?.let {
                        getDataColumn(
                            it,
                            uri,
                            "",
                            emptyArray()
                        )
                    }

                    if (filePath.isNullOrEmpty()) {
                        return BaseAppController.applicationInstance?.let {
                            FileUtils.getFilePathFromURI(
                                it,
                                uri
                            )
                        }
                    } else {
                        return filePath
                    }
                } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                    return uri.path
                }
            } catch (e: Exception) {
                AppLog.errorLog(e.message, e)
                return null
            }

            return null
        }



    }



}