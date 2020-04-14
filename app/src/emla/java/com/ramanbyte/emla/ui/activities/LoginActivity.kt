package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityLoginBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.ui.fragments.PreAssessmentTestFragment
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.utilities.AppLog

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LoginActivity:BaseActivity<ActivityLoginBinding,LoginViewModel>(authModuleDependency) {
    override val viewModelClass: Class<LoginViewModel> = LoginViewModel::class.java

    override fun layoutId(): Int  = R.layout.activity_login

    override fun initiate() {



    }

    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, LoginActivity::class.java)
        }
    }

}