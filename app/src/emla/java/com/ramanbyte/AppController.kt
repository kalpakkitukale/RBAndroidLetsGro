package com.ramanbyte

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.GsonBuilder
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.view_model.factory.ViewModelFactory
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.utilities.AppLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.BuildConfig
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/2/20
 */
class AppController : BaseAppController(), KodeinAware {

    var BASE_URL = "http://webapp.classroomplus.in/clientmgmt/Dev/api/"

    override val kodein: Kodein = Kodein.lazy(true) {

        import(androidXModule(this@AppController))

        bind("app-context") from singleton { this@AppController }

        bind() from singleton {
            OkHttpClient.Builder()
                .addInterceptor(NetworkConnectionInterceptor(instance()))
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .connectTimeout(1, TimeUnit.MINUTES)//10 Minutes
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
        }
        bind() from singleton { GsonConverterFactory.create(GsonBuilder().create()) }
        /*Database instance creation*/
        bind() from singleton {
            ApplicationDatabase(
                this@AppController
            )
        }
       /* bind() from singleton { CompanyDatabaseRepository(this@AppController) }
        bind() from singleton { OfficeRepository(this@AppController) }*/

        bind() from singleton { ViewModelFactory(instance()) }
    }
    override fun onCreate() {
        super.onCreate()
        //getUpFireBaseRemoteConfig()

    }

    private fun getUpFireBaseRemoteConfig(): FirebaseRemoteConfig {
        if (mFirebaseRemoteConfig == null) {
            AppLog.infoLog("firebase initialized")
            // Get Remote Config instance.
            // [START get_remote_config_instance]
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            // [END set_default_values]

            mFirebaseRemoteConfig?.apply {
                // Create a Remote Config Setting to enable developer mode, which you can use to increase
                // the number of fetches available per hour during development. Also use Remote Config
                // Setting to set the minimum fetch interval.
                // [START enable_dev_mode]
                if (BuildConfig.DEBUG) {
                    setConfigSettingsAsync(
                        FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(360)
                            .build()
                    )
                }
                // [END enable_dev_mode]

                // Set default Remote Config parameter values. An app uses the in-app default values, and
                // when you need to adjust those defaults, you set an updated value for only the values you
                // want to change in the Firebase console.
                // [START set_default_values]
                /*setDefaultsAsync(R.xml.remote_config_defaults)*/
                // [END set_default_values]
            }

            fetchAndActivateRemoteMessages()
        }

        return mFirebaseRemoteConfig as FirebaseRemoteConfig
    }

    private fun fetchAndActivateRemoteMessages() {
        // [START fetch_config_with_callback]
        if (mFirebaseRemoteConfig == null) {
            getUpFireBaseRemoteConfig()
        }
        mFirebaseRemoteConfig?.fetchAndActivate()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                AppLog.debugLog("Config params updated: $updated")
                AppLog.infoLog("Fetch and activate succeeded")
            } else {
                AppLog.errorLog("Fetch failed")
            }
        }
        // [END fetch_config_with_callback]
    }
}