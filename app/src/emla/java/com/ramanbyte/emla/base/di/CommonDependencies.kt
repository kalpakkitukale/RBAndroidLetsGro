package com.ramanbyte.emla.base.di

import org.kodein.di.Kodein

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 29/2/20
 */

private const val DOMAIN = "http://webapp.classroomplus.in/"
private const val EMLA = "emla/"
private const val API = "/api/"
private const val LOGIN_CONTROLLER = "Login/"
var CLIENT_BASE = "test"

val repositoryDependencies = Kodein.Module("", true) {

//    bind<PlacementApiController>() with singleton {
//        RetrofitInitializer.invoke(
//            instance(),//db
//            PlacementApiController::class.java,
//            DOMAIN + PLACEMENT + CLIENT_BASE + API
//        )
//    }

}