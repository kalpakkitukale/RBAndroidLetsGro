package com.ramanbyte.emla.view_model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.SkillsRepository
import com.ramanbyte.emla.models.response.SkillsModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_SKILL_ID
import com.ramanbyte.utilities.NetworkConnectivity
import org.kodein.di.generic.instance

class SkillsViewModel(mContext: Context) : BaseViewModel(mContext) {
    override var noInternetTryAgain: () -> Unit = { skillsRepository.tryAgain() }

    private val skillsRepository: SkillsRepository by instance()

    var searchSkillQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        searchSkillQuery.observeForever {
            skillsRepository.searchSkills(it)
        }
    }

    fun getSkillsList(searchStr: String) = run {

        skillsRepository.getSkillsList(searchStr)

        skillsRepository.getPaginationResponseHandler().observeForever {
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

    fun getSkillsList(): LiveData<PagedList<SkillsModel>>? = skillsRepository.getSkillPagedList()

    fun onSkillClick(view: View, skillsModel: SkillsModel) {
        skillsModel.let { model ->
            if (NetworkConnectivity.isConnectedToInternet()) {
                if (model.totalNumberOfJobs!! > 0) {
                    view.findNavController()
                        .navigate(
                            R.id.action_skillFragment_to_jobFragment,
                            Bundle().apply {
                                putInt(KEY_SKILL_ID, model.skillId ?: 0)
                            })

                } else {
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.drawable(R.drawable.ic_no_data)!!,
                        true,
                        BindingUtils.string(R.string.yes), {
                            isAlertDialogShown.postValue(false)
                        },
                        BindingUtils.string(R.string.no), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
            } else {
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.no_internet_message),
                    BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                    true,
                    BindingUtils.string(R.string.yes), {
                        isAlertDialogShown.postValue(false)
                    },
                    BindingUtils.string(R.string.no), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            }
        }
    }

}