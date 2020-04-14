package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.ChaptersRepository
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class ChaptersViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val chaptersRepository: ChaptersRepository by instance()

    override var noInternetTryAgain: () -> Unit = {
        chaptersRepository.tryAgain()
    }

    fun getList(courseId: Int) = run {

        chaptersRepository.getList(courseId)

        chaptersRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {
                paginationResponse(
                    it, PaginationMessages(
                        BindingUtils.string(R.string.no_chapter),
                        BindingUtils.string(R.string.no_more_chapter),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
            }
        }
    }

    fun getList(): LiveData<PagedList<ChaptersModel>>? = chaptersRepository.getList()

    fun onCardClicked(view: View, chaptersModel: ChaptersModel) {
        AppLog.infoLog("Chapter Clicked")
    }

    fun onDownloadClicked(view: View, chaptersModel: ChaptersModel) {
        AppLog.infoLog("Chapter Content Clicked")
    }
}