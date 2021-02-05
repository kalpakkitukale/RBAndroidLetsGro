package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance
import java.io.File

class MyDownloadsViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val contentRepository: ContentRepository by instance()

    val playOrPreviewLiveData = MutableLiveData<MediaInfoModel>(null)
    val deleteMediaLiveData = MutableLiveData<MediaInfoModel>(null)

    fun getMedias(): List<MediaInfoModel>? {

        val listMedia = contentRepository.getMedias()

        return if (listMedia.isNotEmpty()) {
            toggleLayoutVisibility(View.VISIBLE, View.GONE, View.GONE, "")
            listMedia.apply {
                forEach {
                    it.apply {

                        mimeType = FileUtils.getMimeType(contentLink) ?: ""

                        if (!(mimeType?.contains(KEY_MEDIA_TYPE_VIDEO) || mimeType?.contains(
                                KEY_MEDIA_TYPE_AUDIO
                            )
                                    )
                        )
                            if (!(mediaUrl.isNotEmpty() && File(mediaUrl).exists())
                            ) {
                                deleteButtonVisibility = View.GONE
                                previewButtonVisibility = View.GONE
                                noFileMessageVisibility = View.VISIBLE
                            }
                    }
                }
            }
        } else {
            toggleLayoutVisibility(
                View.GONE,
                View.VISIBLE,
                View.GONE,
                BindingUtils.string(R.string.you_dont_have_downloads)
            )
            null
        }

    }

    fun playMedia(mediaInfoModel: MediaInfoModel) {
        playOrPreviewLiveData.value = mediaInfoModel
    }

    fun deleteMedia(mediaInfoModel: MediaInfoModel) {

        if (NetworkConnectivity.isConnectedToInternet()) {
            setAlertDialogResourceModelMutableLiveData(
                BindingUtils.string(R.string.delete_content_alert),
                null,
                false,
                positiveButtonText = BindingUtils.string(R.string.strYes),
                positiveButtonClickFunctionality = {
                    deleteMediaLiveData.value = mediaInfoModel
                    isAlertDialogShown.value = false
                },
                negativeButtonText = BindingUtils.string(R.string.strNo),
                negativeButtonClickFunctionality = {
                    isAlertDialogShown.value = false
                })

            isAlertDialogShown.value = true
        } else {
            showDeleteErrorDialog(
                BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet)
            )
        }
    }

    fun deleteMediaInfo(mediaId: Int): Int {
        return contentRepository.deleteMediaInfo(mediaId)
    }

    fun showDeleteSuccessDialog() {

        setAlertDialogResourceModelMutableLiveData(
            BindingUtils.string(R.string.content_deleted_successfully),
            BindingUtils.drawable(R.drawable.ic_success),
            true,
            positiveButtonText = BindingUtils.string(R.string.strOk),
            positiveButtonClickFunctionality = {
                isAlertDialogShown.value = false
            })

        isAlertDialogShown.value = true

    }

    fun showDeleteErrorDialog(message: String = BindingUtils.string(R.string.failed_to_delete)) {

        setAlertDialogResourceModelMutableLiveData(
            message,
            BindingUtils.drawable(R.drawable.ic_no_internet),
            true,
            positiveButtonText = BindingUtils.string(R.string.strOk),
            positiveButtonClickFunctionality = {
                isAlertDialogShown.value = false
            })

        isAlertDialogShown.value = true
    }

    override var noInternetTryAgain: () -> Unit = {}

}