package com.ramanbyte.emla.base.di

import com.ramanbyte.emla.data_layer.repositories.QuizRepository
import com.ramanbyte.emla.view_model.factory.ViewModelFactory
import com.ramanbyte.utilities.DI_ACTIVITY_CONTEXT
import com.ramanbyte.utilities.DI_AUTH_MODULE
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 13-04-2020
 */
val authModuleDependency = Kodein.Module(DI_AUTH_MODULE, true) {

    import(repositoryDependencies, true)

    bind<QuizRepository>() with singleton{
        QuizRepository(instance())
    }

    bind(overrides = true) from singleton {
        ViewModelFactory(
            instance(
                DI_ACTIVITY_CONTEXT
            )
        )
    }
}