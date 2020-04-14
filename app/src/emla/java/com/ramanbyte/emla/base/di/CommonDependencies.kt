package com.ramanbyte.emla.base.di

import com.ramanbyte.data_layer.network.init.RetrofitInitializer
import com.ramanbyte.emla.data_layer.network.api_layer.ChaptersController
import com.ramanbyte.emla.data_layer.network.api_layer.CoursesController
import com.ramanbyte.emla.data_layer.network.api_layer.LoginApiController
import com.ramanbyte.emla.data_layer.network.api_layer.SectionsController
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 13-04-2020
 */

private const val DOMAIN = "http://webapp.classroomplus.in/"
private const val EMLA = "eMarketPlace/"
private const val API = "/api/"
private const val LOGIN = "Login/"
private const val COURSE = "Course/"
private const val CHAPTER = "Chapter/"
private const val SECTION = "Section/"

var CLIENT_BASE = "Dev"

val repositoryDependencies = Kodein.Module("", true) {

    import(controllersDependencies, true)

}

private val controllersDependencies = Kodein.Module("controllers_dependencies", true) {

    bind<LoginApiController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            LoginApiController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + LOGIN
        )
    }

    bind<CoursesController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            CoursesController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + COURSE
        )
    }

    bind<ChaptersController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            ChaptersController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + CHAPTER
        )
    }

    bind<SectionsController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            SectionsController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + SECTION
        )
    }
}