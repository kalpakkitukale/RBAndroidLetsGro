package com.ramanbyte.emla.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.room.util.FileUtil
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.ui.activities.FileViewerActivity
import com.ramanbyte.emla.ui.activities.MediaPlaybackActivity
import com.ramanbyte.emla.view_model.ChaptersSectionViewModel
import com.ramanbyte.emla.view_model.ChaptersViewModel
import com.ramanbyte.emla.view_model.ContentViewModel
import com.ramanbyte.utilities.*
import java.io.File
import java.util.*

class ContentViewer(
    private val context: Context
) {
    private var viewModel: ContentViewModel? = null

    private var chaptersViewModel: ChaptersViewModel? = null

    private var chaptersSectionViewModel: ChaptersSectionViewModel? = null

    constructor(context: Context, viewModel: ContentViewModel) : this(context) {
        this.viewModel = viewModel
    }

    constructor(context: Context, chaptersViewModel: ChaptersViewModel) : this(context) {
        this.chaptersViewModel = chaptersViewModel
    }

    constructor(
        context: Context,
        chaptersSectionViewModel: ChaptersSectionViewModel
    ) : this(context) {
        this.chaptersSectionViewModel = chaptersSectionViewModel
    }

    fun download(
        contentModel: ContentModel,
        mediaInfoModel: MediaInfoModel
    ): Boolean {

        var isDownloadStarted = false

        var status: Long = -1

        contentModel.downloadVisibility = View.GONE

        mapContentToLocal(contentModel, mediaInfoModel)

        status = viewModel?.addMediaInfo(mediaInfoModel) ?: chaptersViewModel?.addMediaInfo(
            mediaInfoModel
        ) ?: chaptersSectionViewModel?.addMediaInfo(mediaInfoModel) ?: 0

        if (!(status == 0.toLong() || status == (-1).toLong())) {

            if (!mediaInfoModel.mediaType.contains(KEY_MEDIA_TYPE_FILE)) {

                initiateAndStartMediaDownload(mediaInfoModel.requestId, mediaInfoModel.mediaUrl)

            } else {
                AppS3Client.createInstance(context).download(
                    contentModel.content_link,
                    mediaInfoModel.mediaUrl,
                    "DownLoading Content ${contentModel.content_link}",
                    468274
                )
            }

            isDownloadStarted = true
        }

        return isDownloadStarted
    }

    private fun initiateAndStartMediaDownload(requestId: String, url: String) {

        val downloadRequests = DownloadRequest(
            requestId,
            DownloadRequest.TYPE_PROGRESSIVE,
            Uri.parse(url),
            Collections.emptyList(),
            null,
            null
        )

        DownloadService.sendAddDownload(
            context,
            ExoDownloadService::class.java,
            downloadRequests,
            true
        )

        DownloadService.start(
            context,
            ExoDownloadService::class.java
        )
    }

    fun preview(mediaInfoModel: MediaInfoModel) {


        val mimeType = FileUtils.getMimeType(mediaInfoModel.contentLink)

        if (mimeType?.contains(KEY_MEDIA_TYPE_VIDEO) == true || mimeType?.contains(
                KEY_MEDIA_TYPE_AUDIO
            ) == true
        ) {

            val intent = Intent(context, MediaPlaybackActivity::class.java).apply {

                putExtra(KEY_MEDIA_ID, mediaInfoModel.mediaId)
                putExtra(KEY_IS_MEDIA_OFFLINE, true)

            }
            context.startActivity(intent)

        } else {

            val fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    BindingUtils.string(R.string.path_authority),
                    File(mediaInfoModel.mediaUrl)
                )
            } else {
                Uri.fromFile(File(mediaInfoModel.mediaUrl))
            }

            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW).apply {
                type = mimeType
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(Intent.EXTRA_STREAM, fileUri)
                setDataAndType(fileUri, mimeType)
            }, "Open file using..."))

        }

    }

    fun preview(contentModel: ContentModel, mediaInfoModel: MediaInfoModel) {

        mapContentToLocal(contentModel, mediaInfoModel, false)

        if (mediaInfoModel.mediaType.contains(KEY_MEDIA_TYPE_FILE)) {

            if (contentModel.isDownloaded) {

                val fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        context,
                        BindingUtils.string(R.string.path_authority),
                        File(mediaInfoModel.mediaUrl)
                    )
                } else {
                    Uri.fromFile(File(mediaInfoModel.mediaUrl))
                }

                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW).apply {
                    type = mediaInfoModel.mimeType
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    setDataAndType(fileUri, mediaInfoModel.mimeType)
                }, "Open file using..."))

            } else {

                val intent = Intent(context, FileViewerActivity::class.java)
                intent.putExtra(KEY_FILE_NAME, contentModel.content_link)

                context.startActivity(intent)
            }
        } else {

            var status: Long = -1

            if (!contentModel.isDownloaded) {
                status = viewModel?.addMediaInfo(mediaInfoModel) ?: chaptersViewModel?.addMediaInfo(
                    mediaInfoModel
                ) ?: chaptersSectionViewModel?.addMediaInfo(mediaInfoModel) ?: 0
            } else {
                status = 1
            }

            if (!(status == 0.toLong() || status == (-1).toLong())) {

                val intent = Intent(context, MediaPlaybackActivity::class.java).apply {

                    putExtra(KEY_MEDIA_ID, contentModel.id)
                    putExtra(KEY_IS_MEDIA_OFFLINE, contentModel.isDownloaded)
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    "Some error occurred in previewing content!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun deleteMediaOrFiles(mediaInfoModel: MediaInfoModel): Boolean {

        val mimeType = FileUtils.getMimeType(mediaInfoModel.contentLink)

        var isDeleteInitiated = false

        if (mimeType?.contains(KEY_MEDIA_TYPE_VIDEO) == true || mimeType?.contains(
                KEY_MEDIA_TYPE_AUDIO
            ) == true
        ) {

            ExoMediaDownloadUtil.loadDownloads(context)

            isDeleteInitiated = ExoMediaDownloadUtil.removeDownloadedFile(
                Uri.parse(mediaInfoModel?.mediaUrl),
                context
            )

        } else {

            val file = File(mediaInfoModel.mediaUrl)

            if (file.exists())
                isDeleteInitiated = file.delete()

        }

        return isDeleteInitiated
    }

    private fun mapContentToLocal(
        contentModel: ContentModel,
        mediaInfoModel: MediaInfoModel, isDownload: Boolean = true
    ) {

        val mimeType = FileUtils.getMimeType(contentModel.content_link)

        val requestId = StaticMethodUtilitiesKtx.getRandomString.invoke()

        mediaInfoModel.apply {
            mediaId = contentModel.id
            seekPosition = 0
            expirationDate = "2020-03-27"
            this@apply.requestId = requestId
            contentLink = contentModel.content_link
            likeVideo = contentModel.isLike ?: KEY_BLANK
            favouriteVideo = contentModel.isFavourite ?: KEY_BLANK
            contentTitle = contentModel.contentTitle ?: KEY_BLANK
            /* createdDateTime = courseContentModel.createdDateTime
             modifiedDateTime = courseContentModel.modifiedDateTime*/

            mediaUrl = when {
                mimeType?.contains(KEY_MEDIA_TYPE_VIDEO) == true -> {

                    mediaType = KEY_MEDIA_TYPE_VIDEO
                    if (isDownload)
                        mediaStatus = Download.STATE_DOWNLOADING
                    StaticMethodUtilitiesKtx.getS3DynamicURL(contentModel.content_link, context)!!
                    /*AppS3Client.createInstance(context)
                        .getFileAccessUrl(contentModel.content_link)!!*/

                }
                mimeType?.contains(KEY_MEDIA_TYPE_AUDIO) == true -> {
                    if (isDownload)
                        mediaStatus = Download.STATE_DOWNLOADING

                    mediaType = KEY_MEDIA_TYPE_AUDIO
                    StaticMethodUtilitiesKtx.getS3DynamicURL(contentModel.content_link, context)!!
                    /*AppS3Client.createInstance(context)
                        .getFileAccessUrl(contentModel.content_link)!!*/

                }
                else -> {
                    if (isDownload)
                        mediaStatus = Download.STATE_COMPLETED

                    mediaType = KEY_MEDIA_TYPE_FILE

                    FileUtils.getNewCreatedFilePath(
                        FileUtils.getFileNameFromPath(
                            FileUtils.getOriginalFileName(
                                contentLink
                            )
                        )
                    )
                }
            }

            AppLog.debugLog("Media URL -------- $mediaUrl")

            this.mimeType = mimeType ?: ""
        }

    }
}