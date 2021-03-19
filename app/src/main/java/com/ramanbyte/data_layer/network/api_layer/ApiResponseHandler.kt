package com.ramanbyte.data_layer.network.api_layer

import com.google.gson.JsonSyntaxException
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_NO_INTERNET_ERROR
import com.ramanbyte.utilities.KEY_SOMETHING_WENT_WRONG_ERROR
import org.json.JSONException
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.jvm.Throws

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
abstract class ApiResponseHandler {

    @Throws(
        JsonSyntaxException::class,
        IllegalStateException::class,
        NoDataException::class,
        NoInternetException::class,
        ApiException::class
    )
    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T? {

        try {
            val response = call.invoke()

            if (response.isSuccessful) {

                val responseData = response.body()

                if (responseData != null) {

                    if (responseData is String && responseData.toString().contains("<html>", true))
                        throw NoInternetException(BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet))

                    return responseData

                } else {
                    throw NoDataException("", response.code())
                }

            } else {
                val message = StringBuilder()

                val errorBody = response.errorBody()?.string()
                if (errorBody != null && isHtmlString(errorBody.toString()) || response.code() == 404)
                    throw ApiException(BindingUtils.string(R.string.some_thing_went_wrong))

                if (errorBody != null && errorBody.toString().contains("404"))
                    throw NoInternetException(BindingUtils.string(R.string.some_thing_went_wrong))

                /**changed to errorBody from response.errorBody()?.string() to display error message*/
                message.append(errorBody?.replace("\"", ""))

                throw ApiException(message.toString(), response.code())
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            throw NoInternetException(BindingUtils.string(R.string.some_thing_went_wrong))
        } catch (e: com.google.gson.JsonSyntaxException) {
            e.printStackTrace()
            throw NoInternetException(
                BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet)
            )
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
            throw NoInternetException(
                BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet)
            )
        } catch (e: java.net.UnknownHostException) {
            e.printStackTrace()
            throw NoInternetException(
                BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet)
            )
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            throw NoInternetException(
                BindingUtils.string(R.string.some_thing_went_wrong)
            )
        } catch (e : java.lang.reflect.UndeclaredThrowableException){
            e.printStackTrace()
            throw NoInternetException(
                BindingUtils.string(R.string.some_thing_went_wrong)
            )
        }
    }

    private fun isHtmlString(responseData: String): Boolean {
        return responseData.contains("<!DOCTYPE")
    }
}