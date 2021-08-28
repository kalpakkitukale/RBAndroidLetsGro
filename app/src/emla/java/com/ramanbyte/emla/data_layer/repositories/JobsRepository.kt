package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.JobsController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.ApplyJobRequestModel
import com.ramanbyte.emla.models.request.JobRequestModel
import com.ramanbyte.emla.models.response.ApplyJobResponseModel
import com.ramanbyte.emla.models.response.JobModel
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class JobsRepository(mContext: Context) : BaseRepository(mContext) {

    private val jobsController: JobsController by instance()
    val userId = applicationDatabase.getUserDao().getCurrentUser()?.userId!!

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    private val myPageSize = 100
    private var jobsPagedList: LiveData<PagedList<JobModel>>? = null

    fun tryAgain() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        jobsPagedList?.value?.dataSource?.invalidate()
    }

    fun getPaginationResponseHandler(): MutableLiveData<PaginationResponseHandler?> =
        paginationResponseHandlerLiveData

    private val jobsModelObservable =
        ObservableField<JobRequestModel>().apply { set(JobRequestModel()) }

    fun getJobsPagedList(): LiveData<PagedList<JobModel>>? = jobsPagedList

    fun getJobList(skillId: Int) {

        val userModel =
            applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

        val pagedDataSourceFactory = PaginationDataSourceFactory(
            jobsModelObservable.apply {

                set(JobRequestModel().apply {
                    this.userId = 1//userModel?.userId ?: 0
                    this.pageSize = myPageSize
                    this.skillId = 0//skillId
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                jobsController.getJobsList(it)
            } ?: arrayListOf()
        }

        jobsPagedList = LivePagedListBuilder(
            pagedDataSourceFactory,
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(myPageSize).build()
        ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun searchJobs(searchString: String) {
        jobsModelObservable.get().apply {
            this?.searchkey = searchString
        }
        jobsPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    suspend fun getJobDetails(jobId: Int): JobModel? {
        val jobModel = apiRequest {
            jobsController.getJobDetails(JobRequestModel().apply {
                this.jobId = jobId
            })
        }
        return if(jobModel?.isNotEmpty() == true) jobModel?.get(0) else null

        /*return JobModel().apply {
            companyDescription = "ebdeufbrfjhrbgfhrjgbrjktngbjrnf"
            companyWebsite = "www.j.com"
        }*/
    }

    suspend fun applyJob(jobId: Int): ApplyJobResponseModel? {

        val applyJobResponseModel = apiRequest {
            jobsController.applyJob(ApplyJobRequestModel(userId, jobId).apply {
                this.jobId = jobId
            })
        }
        return if(applyJobResponseModel?.isNotEmpty() == true) applyJobResponseModel?.get(0) else null
    }
}