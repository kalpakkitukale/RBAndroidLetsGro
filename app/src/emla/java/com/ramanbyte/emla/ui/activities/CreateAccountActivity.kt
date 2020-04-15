package com.ramanbyte.emla.ui.activities

import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityCreateAccountBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.CreateAccountViewModel

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
class CreateAccountActivity :BaseActivity<ActivityCreateAccountBinding,CreateAccountViewModel>(authModuleDependency){

    override val viewModelClass: Class<CreateAccountViewModel> = CreateAccountViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_create_account

    override fun initiate() {

    }
}