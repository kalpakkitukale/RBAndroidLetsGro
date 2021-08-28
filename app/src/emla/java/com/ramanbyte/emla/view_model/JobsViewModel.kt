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
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.JobsRepository
import com.ramanbyte.emla.models.response.ApplyJobResponseModel
import com.ramanbyte.emla.models.response.JobModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

class JobsViewModel(mContext: Context) : BaseViewModel(mContext) {

    private var jobId: Int = 0

    private val _companyDescriptionLiveData = MutableLiveData<JobModel?>()
    val companyDescriptionLiveData = _companyDescriptionLiveData

    private val _applyJobResponseModelLiveData = MutableLiveData<ApplyJobResponseModel?>()
    val applyJobResponseModelLiveData = _applyJobResponseModelLiveData

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
                        R.id.action_jobListFragment_to_companyDescriptionFragment,
                        Bundle().apply {
                            putInt(KEY_JOB_ID, model.jobId ?: 0)
                            putInt(KEY_IS_JOB_APPLIED, model.isJobApplied ?: 0)
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

    fun onDownloadClick() {

    }

    fun getJobDetails(jobId: Int) {
        this.jobId = jobId
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_job_details))
                _companyDescriptionLiveData.postValue(jobsRepository.getJobDetails(jobId))

                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    "",
                    View.GONE
                )

                coroutineToggleLoader()

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)

                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )

                coroutineToggleLoader()

            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.no_internet_message),
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.course_details_unavailable),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }
    }

    fun applyJob() {
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.apply))
                _applyJobResponseModelLiveData.postValue(jobsRepository.applyJob(jobId))

                coroutineToggleLoader()

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                showApplyJobError()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                showApplyJobError()
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                showApplyJobError()
            }
        }
    }

    private fun showApplyJobError() {
        setAlertDialogResourceModelMutableLiveData(
            message = BindingUtils.string(R.string.some_thing_went_wrong),
            alertDrawableResource = BindingUtils.drawable(R.drawable.ic_fail),
            isInfoAlert = true,
            positiveButtonText = BindingUtils.string(R.string.strOk),
            positiveButtonClickFunctionality = {
                isAlertDialogShown.postValue(false)
            })
        isAlertDialogShown.postValue(true)
    }
}