package com.ramanbyte.emla.data_layer.network.init

import android.content.Context
import android.net.ConnectivityManager
import com.ramanbyte.BaseAppController
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_NO_INTERNET_ERROR
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @addedBy Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
class NetworkConnectionInterceptor(val context: Context) : Interceptor {

    private val applicationContext = context.applicationContext


    @Throws(NoInternetException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            if (!isInternetAvailable())
                throw NoInternetException("No Internet Connection")

            val originalRequest = chain.request()

            val authorizedRequest = originalRequest.newBuilder()
                .header(
                    "Authorization",
                    "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIzOTVhZWUxZS1lNmI5LTQ3ODItYjBiNy0wNzAwNDIwNjJiMTkiLCJzdWIiOiJzdXBlcmFkbWluQHBpYm0uaW4iLCJqdGkiOiI2NWQxZWVmNi01MjViLTQxMmUtOGUyYi1hMTJlMzk4YTlkMmIiLCJlbWFpbCI6InN1cGVyYWRtaW5AcGlibS5pbiIsImlhdCI6IjEiLCJleHAiOjE4NDEzMTA2NDQsImlzcyI6IlBJQk0iLCJhdWQiOiJQSUJNIn0.8IKHm6TFRuokD1B-ATtnABbJLmhme0xis3zWhxTp55c"
                    /*SharedPreferencesDatabase.getStringPref(SharedPreferencesDatabase.KEY_AUTH_TOKEN)*/
                )
                .method(originalRequest.method(), originalRequest.body())
                .build()

            return chain.proceed(authorizedRequest)
        }catch (e : Exception){
            AppLog.infoLog("Interceptor: Exception :: Log")
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            throw NoInternetException(BaseAppController?.mFirebaseRemoteConfig!!.getString(KEY_NO_INTERNET_ERROR).replace("\\n", "\n"))
        }
    }

    fun isInternetAvailable() : Boolean{
        val isInternetAvailable: Boolean

        try {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            isInternetAvailable = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isAvailable && connectivityManager.activeNetworkInfo.isConnected
        }catch (e : Exception){
            throw NoInternetException("No Internet Connection")

        }
        return isInternetAvailable
    }
}