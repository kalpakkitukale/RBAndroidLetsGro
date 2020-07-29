package com.ramanbyte.utilities

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import com.google.android.gms.common.util.IOUtils
import com.ramanbyte.BaseAppController
import com.ramanbyte.utilities.DateUtils.DATE_TIME_SECONDS_PATTERN_FOR_FILE
import java.io.*
import java.io.File.separator
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


/**
 * @author Mansi Manakiki Mody
 * @since 18 Dec 2019
 */
/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 26/02/2020
 */
object FileUtils {

    //File Extension keys
    const val KEY_JPG_IMAGE_EXTENSION = ".jpg"
    const val KEY_JPEG_IMAGE_EXTENSION = ".jpeg"
    const val KEY_PNG_IMAGE_EXTENSION = ".png"
    const val KEY_ZIP_EXTENSION = ".zip"
    const val KEY_RAR_EXTENSION = ".rar"
    const val KEY_SEVENZ_EXTENSION = ".7Z"
    const val KEY_TAR_EXTENSION = ".gz"
    const val KEY_TEXT_EXTENSION = ".txt"
    const val KEY_RICH_TEXT_EXTENSION = ".rtf"
    const val KEY_SPREADSHEET_XLSX_EXTENSION = ".xlsx"
    const val KEY_SPREADSHEET_ODS_EXTENSION = ".ods"
    const val KEY_SPREADSHEET_XLS_EXTENSION = ".xls"
    const val KEY_DOCUMENT_DOCX_EXTENSION = ".docx"
    const val KEY_DOCUMENT_DOC_EXTENSION = ".doc"
    const val KEY_DOCUMENT_ODT_EXTENSION = ".odt"
    const val KEY_PRESENTATION_PPTX_EXTENSION = ".pptx"
    const val KEY_PRESENTATION_PPT_EXTENSION = ".ppt"
    const val KEY_PRESENTATION_ODP_EXTENSION = ".odp"
    const val KEY_PDF_DOCUMENT_EXTENSION = ".pdf"
    const val KEY_HIDDEN_FILE_EXTENSION = ".nomedia"

    const val KEY_UNIQUE_FILE_NAME_ADDITION_STRING = "_5_6_9"
    const val KEY_LOCAL_FILE_INITIALS = "CPM"
    private const val KEY_BUFFER = 2048
     const val PATH_SEPARATOR = "/"

    fun getNewCreatedFilePath(fileName: String): String {
        return try {
            val newFile =
                File(BaseAppController.applicationInstance?.getExternalFilesDir(null), fileName)
            if (!newFile.exists()) newFile.createNewFile()

            newFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            ""
        }
    }

    fun getNewCreatedImageFilePath(customFileName: String): String {
        var fileName = customFileName
        if (fileName.isEmpty()) {
            fileName = "$KEY_LOCAL_FILE_INITIALS$KEY_UNIQUE_FILE_NAME_ADDITION_STRING" +
                    "${DateUtils.getCurrentDateTime(DATE_TIME_SECONDS_PATTERN_FOR_FILE)}$KEY_JPG_IMAGE_EXTENSION"
        }
        return getNewCreatedFilePath(fileName)
    }

    fun createHiddenFile() {
        getNewCreatedFilePath(KEY_HIDDEN_FILE_EXTENSION)
    }

    fun getFileNameFromPath(filePath: String?): String {
        return try {
            if (filePath!!.isNotEmpty()) {
                val index: Int = filePath.lastIndexOf(PATH_SEPARATOR)
                filePath.substring(index + 1)
            } else {
                ""
            }
        } catch (stringIndexOutOfBoundsException: StringIndexOutOfBoundsException) {
            stringIndexOutOfBoundsException.printStackTrace()
            AppLog.errorLog(
                stringIndexOutOfBoundsException.message,
                stringIndexOutOfBoundsException
            )
            ""
        } catch (nullPointerException: NullPointerException) {
            nullPointerException.printStackTrace()
            AppLog.errorLog(nullPointerException.message, nullPointerException)
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            ""
        }

    }

    fun getOriginalFileName(serverUniqueFileName: String): String {
        return try {
            if (serverUniqueFileName.contains(KEY_UNIQUE_FILE_NAME_ADDITION_STRING)
            ) {
                val fileExtension =
                    serverUniqueFileName.substring(serverUniqueFileName.lastIndexOf("."))
                val fileName = serverUniqueFileName.substring(
                    0,
                    serverUniqueFileName.lastIndexOf(KEY_UNIQUE_FILE_NAME_ADDITION_STRING)
                ).trim { it <= ' ' }
                fileName + fileExtension
            } else {
                serverUniqueFileName
            }
        } catch (stringIndexOutOfBoundsException: StringIndexOutOfBoundsException) {
            stringIndexOutOfBoundsException.printStackTrace()
            AppLog.errorLog(
                stringIndexOutOfBoundsException.message,
                stringIndexOutOfBoundsException
            )
            serverUniqueFileName
        } catch (nullPointerException: NullPointerException) {
            nullPointerException.printStackTrace()
            AppLog.errorLog(nullPointerException.message, nullPointerException)
            serverUniqueFileName
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            serverUniqueFileName
        }
    }

    fun extractFilesFromZip(zipFilePath: String): ArrayList<String> {
        return try {
            var extractFile = ArrayList<String>()
            var fileName: String
            val `is`: InputStream = FileInputStream(zipFilePath)
            val zis =
                ZipInputStream(BufferedInputStream(`is`))
            var mZipEntry: ZipEntry
            val buffer = ByteArray(KEY_BUFFER)
            var count: Int
            while (zis.nextEntry.also { mZipEntry = it } != null) {
                fileName = mZipEntry.name
                fileName = getOriginalFileName(fileName)
                if (mZipEntry.isDirectory) {
                    val fmd = File(getNewCreatedFilePath(fileName))
                    fmd.mkdirs()
                    continue
                }
                val unzipFileEntryPath: String = getNewCreatedFilePath(fileName)
                AppLog.infoLog("Unzip Entry: $fileName")
                AppLog.infoLog("Unzip Entry Path: $unzipFileEntryPath")
                val file = File(unzipFileEntryPath)
                if (!file.exists()) {
                    val fout =
                        FileOutputStream(unzipFileEntryPath)
                    while (zis.read(buffer).also { count = it } != -1) {
                        fout.write(buffer, 0, count)
                    }
                    fout.close()
                    zis.closeEntry()
                }
                AppLog.infoLog("File Name ------  ${getFileNameFromPath(unzipFileEntryPath)}")
                extractFile.add(unzipFileEntryPath)
            }
            zis.close()
            ArrayList()
        } catch (e: IOException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            ArrayList()
        }
    }

    fun createZipFileFromFiles(customFileName: String, filePathsList: ArrayList<String>) {
        try {
            var zipFileName = customFileName
            if (zipFileName.isEmpty()) {
                zipFileName = "$KEY_LOCAL_FILE_INITIALS$KEY_UNIQUE_FILE_NAME_ADDITION_STRING" +
                        "${DateUtils.getCurrentDateTime(DATE_TIME_SECONDS_PATTERN_FOR_FILE)}$KEY_ZIP_EXTENSION"
            }

            val zipFilePath = getNewCreatedFilePath(zipFileName)
            if (zipFilePath.isNotEmpty()) {
                var origin: BufferedInputStream
                val dest = FileOutputStream(zipFilePath)
                val out =
                    ZipOutputStream(BufferedOutputStream(dest))
                val data = ByteArray(KEY_BUFFER)
                for (i in filePathsList.indices) {
                    val fi = FileInputStream(filePathsList[i])
                    origin = BufferedInputStream(fi, KEY_BUFFER)
                    val entry = ZipEntry(
                        filePathsList[i].substring(
                            filePathsList[i].lastIndexOf("/") + 1
                        )
                    )
                    out.putNextEntry(entry)
                    var count: Int
                    while (origin.read(
                            data,
                            0,
                            KEY_BUFFER
                        ).also { count = it } != -1
                    ) {
                        out.write(data, 0, count)
                    }
                    origin.close()
                }
                out.close()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    private fun copyUriToFilePath(
        context: Context,
        contentUri: Uri?
    ): String? { //copy file and send new file path
        val fileName = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(getNewCreatedFilePath(fileName!!))
            copy(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    private fun copy(
        context: Context,
        srcUri: Uri?,
        dstFile: File?
    ) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            IOUtils.copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getAbsolutePathFormURI(uri: Uri): String? {
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
                        copyUriToFilePath(
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
            return ""
        }

        return ""
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
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    val value = cursor.getString(columnIndex)
                    return if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith(
                            "file://"
                        )
                    ) {
                        ""
                    } else value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return ""
    }

    fun getFilePathFromURI(
        context: Context,
        contentUri: Uri?
    ): String? { //copy file and send new file path
        val fileName = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(getApplicationFolder() + separator.toString() + fileName)
            copy(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    fun getApplicationFolder(): String {
        return Environment.getExternalStorageDirectory().toString() +
                PATH_SEPARATOR + KEY_APP_STORAGE_FOLDER
    }

    fun getMimeType(extension: String): String? {
        val ext = extension.substringAfterLast(".")
        var type: String? = ""
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        AppLog.infoLog("Image Type ==${type}")
        return type
    }

    fun getFileExtension(file: File): String {
        val name = file.name
        try {
            return name.substring(name.lastIndexOf(".") + 1)
        } catch (e: Exception) {
            return ""
        }
    }

    fun getNewCreatedPdfFilePath(customFileName: String): String {
        var fileName = customFileName
        if (fileName.isEmpty()) {
            fileName = "$KEY_LOCAL_FILE_INITIALS$KEY_UNIQUE_FILE_NAME_ADDITION_STRING" +
                    "${DateUtils.getCurrentDateTime(DATE_TIME_SECONDS_PATTERN_FOR_FILE)}$KEY_PDF_DOCUMENT_EXTENSION"
        }
        return getNewCreatedFilePath(fileName)
    }
}