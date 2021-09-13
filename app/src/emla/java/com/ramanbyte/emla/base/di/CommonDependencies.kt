package com.ramanbyte.emla.base.di

import com.ramanbyte.BuildConfig
import com.ramanbyte.data_layer.network.init.RetrofitInitializer
import com.ramanbyte.emla.data_layer.network.api_layer.*
import com.ramanbyte.emla.data_layer.repositories.*
import com.ramanbyte.emla.faculty.data_layer.network.api_layer.FacultyCoursesController
import com.ramanbyte.emla.faculty.data_layer.network.api_layer.FacultyMasterApiController
import com.ramanbyte.emla.faculty.data_layer.network.api_layer.FacultyQuestionController
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyCoursesRepository
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyQuestionRepository
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 13-04-2020
 */

private const val DOMAIN = BuildConfig.DOMAIN_URL
private const val EMLA = "eMarketPlace/"
private const val PLACEMENT = "letsgrojob/"
private const val API = "/api/"
private const val LOGIN = "Login/"
private const val REGISTRATION = "Registration/"
private const val COURSE = "Course/"
private const val CHAPTER = "Chapter/"
private const val SECTION = "Section/"
private const val QUESTION = "Question/"
private const val MASTER = "Master/"
private const val SKILLS = "Skills/"
private const val JOB = "Job/"
private const val QUIZ = "Quiz/"

var CLIENT_BASE = BuildConfig.CLIENT_BASE
var PLACEMENT_CLIENT_BASE = BuildConfig.JOB_CLIENT_BASE

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

    bind<QuestionRepository>() with singleton {
        QuestionRepository(instance())
    }

    bind<QuizRepository>() with singleton {
        QuizRepository(instance())
    }

    bind<CourseQuizRepository>() with singleton {
        CourseQuizRepository(instance())
    }

    bind<FacultyCoursesRepository>() with singleton {
        FacultyCoursesRepository(instance())
    }

    bind<FacultyQuestionRepository>() with provider {
        FacultyQuestionRepository(instance())
    }

    bind<SkillsRepository>() with provider {
        SkillsRepository(instance())
    }

    bind<JobsRepository>() with provider {
        JobsRepository(instance())
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

    bind<CourseQuizController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            CourseQuizController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + QUIZ
        )
    }

    bind<SkillsController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            SkillsController::class.java,
            DOMAIN + PLACEMENT + PLACEMENT_CLIENT_BASE + API + SKILLS
        )
    }

    bind<JobsController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            JobsController::class.java,
            DOMAIN + PLACEMENT + PLACEMENT_CLIENT_BASE + API + JOB
        )
    }

    bind<FacultyCoursesController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            FacultyCoursesController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + COURSE
        )
    }


    /*
    * Faculty Side API Controller ---- Start ----
    * */
    bind<FacultyMasterApiController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            FacultyMasterApiController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + MASTER
        )
    }

    bind<FacultyQuestionController>() with singleton {
        RetrofitInitializer.invoke(
            instance(),//db
            FacultyQuestionController::class.java,
            DOMAIN + EMLA + CLIENT_BASE + API + QUESTION
        )
    }
    /*
   * Faculty Side API Controller ---- End ----
   * */
}