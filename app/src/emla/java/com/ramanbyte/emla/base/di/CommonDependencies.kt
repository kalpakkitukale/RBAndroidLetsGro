package com.ramanbyte.emla.base.di

import com.ramanbyte.data_layer.network.init.RetrofitInitializer
import com.ramanbyte.emla.data_layer.network.api_layer.ChaptersController
import com.ramanbyte.emla.data_layer.network.api_layer.CoursesController
import com.ramanbyte.emla.data_layer.network.api_layer.LoginApiController
import com.ramanbyte.emla.data_layer.network.api_layer.SectionsController
import com.ramanbyte.emla.data_layer.repositories.ChaptersRepository
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.data_layer.network.api_layer.*
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
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
private const val REGISTRATION = "Registration/"
private const val COURSE = "Course/"
private const val CHAPTER = "Chapter/"
private const val SECTION = "Section/"
private const val QUESTION = "Question/"

var CLIENT_BASE = "Test"

val repositoryDependencies = Kodein.Module("", true) {

    import(controllersDependencies, true)

    bind<CoursesRepository>() with singleton {
        CoursesRepository(instance())
    }

    bind<ChaptersRepository>() with singleton {
        ChaptersRepository(instance())
    }

    bind<SectionsRepository>() with singleton {
        SectionsRepository(instance())
    }

    bind<ContentRepository>() with singleton {
        ContentRepository(instance())
    }

}

private val controllersDependencies = Kodein.Module("controllers_dependencies", true) {

    bind<LoginApiController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            LoginApiController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + LOGIN
        )
    }
    bind<RegistrationController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            RegistrationController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + REGISTRATION
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

    bind<QuestionController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            QuestionController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + QUESTION
        )
    }
}