package com.ramanbyte.emla.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.JobSkillsRepository
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.models.response.SkillsModel
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class JobSkillsViewModel(mContext: Context) : BaseViewModel(mContext) {
    override var noInternetTryAgain: () -> Unit = { jobSkillsRepository.tryAgain() }

    private val jobSkillsRepository: JobSkillsRepository by instance()

    fun getSkillsList(searchStr: String) = run {

        jobSkillsRepository.getSkillsList(searchStr)

        jobSkillsRepository.getPaginationResponseHandler().observeForever {
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

    fun getSkillsList(): LiveData<PagedList<SkillsModel>>? = jobSkillsRepository.getList()
}