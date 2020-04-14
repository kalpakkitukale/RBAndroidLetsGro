package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.SectionsModel
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class ChaptersSectionViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val sectionsRepository: SectionsRepository by instance()

    override var noInternetTryAgain: () -> Unit = {
        sectionsRepository.tryAgain()
    }

    fun getList(chapterId: Int) = run {

        sectionsRepository.getList(chapterId)

        sectionsRepository.getPaginationResponseHandler().observeForever {
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

    fun getList(): LiveData<PagedList<SectionsModel>>? = sectionsRepository.getList()

    fun onCardClicked(view: View, sectionsModel: SectionsModel) {

    }

    fun onDownloadClicked(view: View, sectionsModel: SectionsModel) {

    }

    fun takeFormativeTest(buttonView: View) {

    }
}