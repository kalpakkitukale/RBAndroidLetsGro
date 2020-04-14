package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityLoginBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.LoginViewModel

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(authModuleDependency) {
    override val viewModelClass: Class<LoginViewModel> = LoginViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_login

    override fun initiate() {
        setListeners()
    }

    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, LoginActivity::class.java)
        }
    }

    private fun setListeners() {
        layoutBinding.apply {
            tvForgetPassword.setOnClickListener {
                startActivity(ForgotPasswordActivity.intent(this@LoginActivity))
            }

            tvChangePassword.setOnClickListener {
                startActivity(ChangePasswordActivity.intent(this@LoginActivity))
            }
        }
    }
}