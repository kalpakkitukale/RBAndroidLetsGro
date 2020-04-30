package com.ramanbyte.emla.view_model

import android.content.Context
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.models.MediaInfoModel
import org.kodein.di.generic.instance

class MediaPlaybackViewModel(mContext: Context) : BaseViewModel(mContext) {

    //Repo
    private val contentRepository: ContentRepository by instance()

    var mediaInfoModel: MediaInfoModel? = null

    fun getMediaInfo(mediaId: Int) {
        mediaInfoModel = contentRepository.getMediaInfo(mediaId)
    }

    fun updateMediaInfo(mediaInfoModel: MediaInfoModel) =
        contentRepository.updateMediaInfo(mediaInfoModel)

    override var noInternetTryAgain: () -> Unit = {}

}