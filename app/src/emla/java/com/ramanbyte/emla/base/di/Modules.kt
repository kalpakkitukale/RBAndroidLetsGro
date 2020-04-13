package com.ramanbyte.emla.base.di

import com.ramanbyte.utilities.DI_AUTH_MODULE
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 29/2/20
 */
val authModuleDependency = Kodein.Module(DI_AUTH_MODULE, true) {

    import(repositoryDependencies, true)

//    bind<OfficeRepository>() with singleton {
//        OfficeRepository(instance("app-context"))
//    }
}