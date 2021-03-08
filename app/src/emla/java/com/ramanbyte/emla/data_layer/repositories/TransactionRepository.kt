package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.TransactionApiController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.TransactionHistoryModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CartRequestModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_ANDROID
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class TransactionRepository(val mContext: Context) : BaseRepository(mContext) {

    private val transactionApiController: TransactionApiController by instance()
    fun getCurrentUser(): UserModel? {
        return applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()
    }

    suspend fun insertTransaction(
        insertTransactionRequestModel: InsertTransactionRequestModel,
        isSuccessTransaction: Boolean
    ): Int {
        val loginResponseModel = applicationDatabase.getUserDao().getCurrentUser()

        val deviceDetails = ""

        insertTransactionRequestModel.createdBy = loginResponseModel!!.userId
        insertTransactionRequestModel.modifyBy = loginResponseModel.userId
        insertTransactionRequestModel.added_By = loginResponseModel.userId
        insertTransactionRequestModel.userId = loginResponseModel.userId
        insertTransactionRequestModel.userId = loginResponseModel.userId
        insertTransactionRequestModel.registrationId = loginResponseModel.userId

        insertTransactionRequestModel.deviceId = 0
        insertTransactionRequestModel.deviceType = KEY_ANDROID
        insertTransactionRequestModel.clientName = KEY_BLANK

        AppLog.debugLog("insertTransactionRequestModel ---- $insertTransactionRequestModel")

        val insertTransactionString = apiRequest {
            transactionApiController.postTransactionDetails(insertTransactionRequestModel)
        }
        AppLog.debugLog("insertTransactionString ---- $insertTransactionString")
        return insertTransactionString ?: 0
    }

    suspend fun getCart(): ArrayList<CartResponseModel>? {
        val userId = getCurrentUser()?.userId ?: 0
        return apiRequest {
            transactionApiController.getCartList(userId)
        }
    }

    suspend fun insertCart(cartRequestModel: CartRequestModel, courseId: Int): Int? {
        val loggedInUserID = getCurrentUser()?.userId ?: 0
        cartRequestModel.apply {
            courseDetailsId = courseId
            userId = loggedInUserID
            createdBy = loggedInUserID
            modifyBy = loggedInUserID
        }
        return apiRequest {
            transactionApiController.insertCart(cartRequestModel)
        }
    }

    suspend fun deleteCart(cartItemId: Int): Int? {
        val userId = getCurrentUser()?.userId ?: 0
        return apiRequest {
            transactionApiController.deleteCart(userId, cartItemId)
        }
    }

    suspend fun getAllTransactionHistory(): List<TransactionHistoryModel>? {
        val userId = getCurrentUser()?.userId ?: 0
        return apiRequest {
            transactionApiController.getAllTransactionHistory(userId)
        }
    }



    private val pageSize = 10
    private val coursesModelObservable =
            ObservableField<CoursesRequest>().apply { set(CoursesRequest()) }
    lateinit var coursesPagedDataSourceFactory: PaginationDataSourceFactory<CoursesModel, CoursesRequest>
    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
            MutableLiveData(null)
    var coursesPagedList: LiveData<PagedList<CoursesModel>>? = null
    private val pageListConfig =
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()


    fun getUser(): UserModel? {
        applicationDatabase.getUserDao().apply {
            return getCurrentUser()?.replicate<UserEntity, UserModel>()
        }
    }
    fun myCourseList() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        coursesPagedDataSourceFactory = PaginationDataSourceFactory(
                coursesModelObservable.apply {
                    val userModel = getUser()
                    set(CoursesRequest().apply {
                        this.userId = userModel?.userId ?: 0
                    })
                },
                paginationResponseHandlerLiveData
        ) {
            apiRequest {
                transactionApiController.mycourseList(it)

            } ?: arrayListOf()
        }

        coursesPagedList =
                LivePagedListBuilder(coursesPagedDataSourceFactory, pageListConfig).build()

    }

    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData


    /*
  * This function is used for to fetch the fresh order history list on try again button
  * */
    fun tryAgain() {
        coursesPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun searchCourse(searchString: String) {
        coursesModelObservable.get().apply {
            this?.searchKey = searchString
        }
        coursesPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

}