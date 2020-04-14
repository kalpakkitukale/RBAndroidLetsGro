package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityForgotPasswordBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.ForgetPasswordViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding, ForgetPasswordViewModel>(
    authModuleDependency
) {

    companion object {

        fun intent(activity: Activity): Intent {

            return Intent(activity, ForgotPasswordActivity::class.java)
        }
    }

    override val viewModelClass: Class<ForgetPasswordViewModel> =
        ForgetPasswordViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_forgot_password

    override fun initiate() {

        ProgressLoader(this, viewModel)
        AlertDialog(this, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@ForgotPasswordActivity

            forgotPasswordViewModel = viewModel

            viewModel.apply {
                isEmailSendSuccessfully.observe(this@ForgotPasswordActivity, Observer {
                    if (it != null) {
                        if (it == true) {
                            isEmailSendSuccessfully.value = null
                            finish()
                        }
                    }
                })
            }

        }
    }
}
