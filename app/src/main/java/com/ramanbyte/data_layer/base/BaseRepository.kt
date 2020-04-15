package com.ramanbyte.data_layer.base

import android.content.Context
import com.ramanbyte.data_layer.network.api_layer.ApiResponseHandler
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.utilities.DI_ACTIVITY_CONTEXT
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

abstract class BaseRepository(mContext: Context) : ApiResponseHandler(), KodeinAware {

    /*override val kodein by kodein(mContext)
    override val kodeinContext = kcontext(mContext)*/

    private val _parentKodein by kodein(mContext)

    override val kodein: Kodein by Kodein.lazy(allowSilentOverride = true) {
        extend(_parentKodein, true, copy = Copy.All)

        bind<Context>(DI_ACTIVITY_CONTEXT) with singleton { mContext }

        import(authModuleDependency, true)
    }

    protected val applicationDatabase: ApplicationDatabase by instance()
}