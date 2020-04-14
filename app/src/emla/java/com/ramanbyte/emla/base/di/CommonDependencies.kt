package com.ramanbyte.emla.base.di

import com.ramanbyte.data_layer.network.init.RetrofitInitializer
import com.ramanbyte.emla.data_layer.network.api_layer.LoginApiController
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 13-04-2020
 */

private const val DOMAIN = "http://webapp.classroomplus.in/"
private const val E_MARKET_PLACE = "eMarketPlace/"
private const val API = "/api/"
private const val LOGIN = "Login/"
var CLIENT_BASE = "Dev"

val repositoryDependencies = Kodein.Module("", true) {

    bind<LoginApiController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            LoginApiController::class.java,
            DOMAIN + E_MARKET_PLACE + CLIENT_BASE + API + LOGIN
        )
    }

}