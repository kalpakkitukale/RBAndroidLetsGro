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
import com.ramanbyte.emla.data_layer.repositories.JobsRepository
import com.ramanbyte.emla.models.CourseResultModel
import com.ramanbyte.emla.models.response.JobModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_JOB_ID
import com.ramanbyte.utilities.NetworkConnectivity
import org.kodein.di.generic.instance

class JobsViewModel(mContext: Context) : BaseViewModel(mContext) {

    var companyDescriptionLiveData = MutableLiveData<JobModel>()

    override var noInternetTryAgain: () -> Unit = { jobsRepository.tryAgain() }

    private val jobsRepository: JobsRepository by instance()

    var searchJobQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        searchJobQuery.observeForever {
            jobsRepository.searchJobs(it)
        }
    }

    fun loadJobsList(skillId: Int) = run {

        jobsRepository.getJobList(skillId)

        jobsRepository.getPaginationResponseHandler().observeForever {
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

    fun getJobsList(): LiveData<PagedList<JobModel>>? = jobsRepository.getJobsPagedList()

    fun onJobClick(view: View, jobModel: JobModel) {
        jobModel.let { model ->
            if (NetworkConnectivity.isConnectedToInternet()) {
                view.findNavController()
                    .navigate(
                        R.id.action_jobListFragment_to_JobDetailFragment,
                        Bundle().apply {
                            putInt(KEY_JOB_ID, model.jobId ?: 0)
                        })
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

    fun onDownloadClick(){

    }

}