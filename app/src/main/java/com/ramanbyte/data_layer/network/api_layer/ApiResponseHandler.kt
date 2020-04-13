package com.ramanbyte.data_layer.network.api_layer

import com.google.gson.JsonSyntaxException
import com.ramanbyte.BaseAppController
import com.ramanbyte.placement.data_layer.network.exception.ApiException
import com.ramanbyte.placement.data_layer.network.exception.NoDataException
import com.ramanbyte.placement.data_layer.network.exception.NoInternetException
import com.ramanbyte.utilities.KEY_NO_INTERNET_ERROR
import com.ramanbyte.utilities.KEY_SOMETHING_WENT_WRONG_ERROR
import org.json.JSONException
import retrofit2.Response
import java.net.SocketTimeoutException

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
                        throw NoInternetException( BaseAppController.mFirebaseRemoteConfig!!.getString(
                            KEY_NO_INTERNET_ERROR
                        ))

                    return responseData

                } else {
                    throw NoDataException("", response.code())
                }

            } else {
                val message = StringBuilder()

                val errorBody = response.errorBody()?.string()
                if (errorBody != null && isHtmlString(errorBody.toString()))
                    throw ApiException(
                        BaseAppController.mFirebaseRemoteConfig!!.getString(
                            KEY_SOMETHING_WENT_WRONG_ERROR))

                if (errorBody != null && errorBody.toString().contains("404"))
                    throw NoInternetException(BaseAppController.mFirebaseRemoteConfig!!.getString(
                        KEY_SOMETHING_WENT_WRONG_ERROR))

                /**changed to errorBody from response.errorBody()?.string() to display error message*/
                message.append(errorBody?.replace("\"", ""))

                throw ApiException(message.toString(), response.code())
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            throw NoInternetException(BaseAppController.mFirebaseRemoteConfig!!.getString(
                KEY_SOMETHING_WENT_WRONG_ERROR))
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            throw NoInternetException(
                BaseAppController.mFirebaseRemoteConfig!!.getString(
                    KEY_NO_INTERNET_ERROR)
            )
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
            throw ApiException(
                BaseAppController.mFirebaseRemoteConfig!!.getString(
                    KEY_SOMETHING_WENT_WRONG_ERROR))
        } catch (e: java.net.UnknownHostException) {
            e.printStackTrace()
            throw NoInternetException(
                BaseAppController.mFirebaseRemoteConfig!!.getString(
                    KEY_NO_INTERNET_ERROR)
            )
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            throw NoInternetException(
                BaseAppController.mFirebaseRemoteConfig!!.getString(
                    KEY_SOMETHING_WENT_WRONG_ERROR))
        }
    }

    private fun isHtmlString(responseData: String): Boolean {
        return responseData.contains("<!DOCTYPE")
    }
}