package com.ramanbyte.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.kcontext

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
abstract class BaseViewModel(
    mContext: Context? = BaseAppController.applicationInstance
) : ViewModel(), KodeinAware {

    override val kodein by kodein(mContext!!)
    override val kodeinContext = kcontext(mContext!!)

    val viewVisibilityLiveData = MutableLiveData<Int>().apply {
        value = View.VISIBLE
    }

    val noDataMutableLiveData = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    val errorVisibilityMutableLiveData = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    val noInternetMutableLiveData = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    val visibilityMessageLiveData = MutableLiveData<String>().apply {
        value = ""
    }

    val isLoaderShowingLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    val loaderMessageLiveData = ObservableField<String>().apply {
        set(BindingUtils.string(R.string.str_loading))
    }

    val isAlertDialogShown = MutableLiveData<Boolean>().apply {
        value = false
    }

    val alertDialogResourceModelMutableLiveData =
        ObservableField<AlertDialog.AlertDialogResourceModel>().apply {
            set(AlertDialog.AlertDialogResourceModel())
        }

    fun setAlertDialogResourceModelMutableLiveData(
        message: String,
        alertDrawableResource: Drawable?,
        isInfoAlert: Boolean,
        positiveButtonText: String,
        positiveButtonClickFunctionality: () -> Unit,
        negativeButtonText: String = BindingUtils.string(R.string.strCancel),
        negativeButtonClickFunctionality: () -> Unit = {},
        alertDrawableResourceSign: Drawable = BindingUtils.drawable(R.drawable.circle_launcher_white)!!
    ) {
        alertDialogResourceModelMutableLiveData.set(
            AlertDialog.AlertDialogResourceModel().apply {
                this.message = message
                this.alertDrawableResource = alertDrawableResource
                this.alertDrawableResourceSign = alertDrawableResourceSign
                this.isInfoAlert = isInfoAlert
                this.positiveButtonText = positiveButtonText
                this.positiveButtonClickFunctionality = positiveButtonClickFunctionality
                this.negativeButtonText = negativeButtonText
                this.negativeButtonClickFunctionality = negativeButtonClickFunctionality
            })
    }

    fun toggleLayoutVisibility(
        viewVisibility: Int,
        noDataVisibility: Int,
        noInternetVisibility: Int,
        message: String,
        errorVisibility: Int = View.GONE
    ) {
        viewVisibilityLiveData.postValue(viewVisibility)
        noDataMutableLiveData.postValue(noDataVisibility)
        noInternetMutableLiveData.postValue(noInternetVisibility)
        errorVisibilityMutableLiveData.postValue(errorVisibility)
        visibilityMessageLiveData.postValue(message)
    }

    fun backPressed(view: View) {
        view.findNavController().navigateUp()
    }

    abstract var noInternetTryAgain: () -> Unit


    /*
   * Pagination Response Handler
   * */
    fun paginationResponse(
        paginationResponseHandler: PaginationResponseHandler,
        paginationMessages: PaginationMessages
    ) {

        when (paginationResponseHandler) {

            PaginationResponseHandler.INIT_LOADING -> {
                isLoaderShowingLiveData.postValue(true)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    KEY_BLANK
                )
            }
            PaginationResponseHandler.INIT_LOADED -> {
                isLoaderShowingLiveData.postValue(false)
                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    KEY_BLANK
                )
            }
            PaginationResponseHandler.INIT_NO_DATA -> {
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    paginationMessages.noDataMessage
                )
                isLoaderShowingLiveData.postValue(false)
            }
            PaginationResponseHandler.INIT_ERROR -> {
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    paginationMessages.errorMessage, View.VISIBLE
                )
                isLoaderShowingLiveData.postValue(false)
            }
            PaginationResponseHandler.INIT_NO_INTERNET -> {
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    paginationMessages.noInternetMessage
                )
                isLoaderShowingLiveData.postValue(false)
            }
            /*PaginationResponseHandler.PAGE_LOADING -> AlertUtilities.showToast(
                mContext!!,
                "PAGE_LOADING"
            )
            PaginationResponseHandler.PAGE_LOADED -> AlertUtilities.showToast(
                mContext!!,
                "PAGE_LOADED"
            )
            PaginationResponseHandler.PAGE_NO_DATA -> AlertUtilities.showToast(
                mContext!!,
                "PAGE_NO_DATA"
            )
            PaginationResponseHandler.PAGE_ERROR -> AlertUtilities.showToast(
                mContext!!,
                "PAGE_ERROR"
            )*/

        }
    }

    fun invokeApiCall(apiCallFunction: suspend () -> Unit) {
        invokeApiCall(true, apiCallFunction = apiCallFunction)
    }

    fun invokeApiCall(showLoader: Boolean = true, apiCallFunction: suspend () -> Unit) {
        CoroutineUtils.main {

            try {

                isLoaderShowingLiveData.postValue(showLoader)

                apiCallFunction.invoke()

                isLoaderShowingLiveData.postValue(false)

            } catch (e: NoInternetException) {

                setAlertDialogResourceModelMutableLiveData(
                    e.message
                        ?: BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                    BindingUtils.drawable(R.drawable.ic_no_internet),
                    isInfoAlert = false,
                    positiveButtonText = BindingUtils.string(R.string.tryAgain),
                    positiveButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                        invokeApiCall(apiCallFunction)
                    },
                    negativeButtonText = BindingUtils.string(R.string.strCancel),
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                /*toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet)
                )*/
                isAlertDialogShown.postValue(true)

                isLoaderShowingLiveData.postValue(false)

            } catch (e: ApiException) {
                isLoaderShowingLiveData.postValue(false)
                setAlertDialogResourceModelMutableLiveData(
                    e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                    BindingUtils.drawable(R.drawable.something_went_wrong),
                    isInfoAlert = false,
                    positiveButtonText = BindingUtils.string(R.string.tryAgain),
                    positiveButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                        invokeApiCall(apiCallFunction)
                    },
                    negativeButtonText = BindingUtils.string(R.string.strCancel),
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                /*toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )*/
                isAlertDialogShown.postValue(true)
            } catch (e: NoDataException) {
                isLoaderShowingLiveData.postValue(false)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.no_data),
                    View.GONE
                )
                //noDataFunction.invoke()
            } catch (e: java.lang.reflect.UndeclaredThrowableException) {

            }

        }
    }
    /*
    * Pagination Response Handler
    * */

    suspend fun coroutineToggleLoader(loaderMessage: String = BindingUtils.string(R.string.str_loading)) {
        withContext(Dispatchers.Main) {
            toggleLoader(loaderMessage)
        }
    }

    fun toggleLoader(loaderMessage: String = BindingUtils.string(R.string.str_loading)) {

        loaderMessageLiveData.set(loaderMessage)

        val loaderStatus = isLoaderShowingLiveData.value!!
        isLoaderShowingLiveData.postValue(!loaderStatus)
//        isLoaderShowingLiveData.value = !loaderStatus

    }
}