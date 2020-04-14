package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ChangePasswordBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.ChangePasswordViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader


class ChangePasswordActivity : BaseActivity<ChangePasswordBinding, ChangePasswordViewModel>(
    authModuleDependency
) {
    override val viewModelClass: Class<ChangePasswordViewModel> =
        ChangePasswordViewModel::class.java

    override fun layoutId(): Int = R.layout.change_password

    companion object {

        fun intent(activity: Activity): Intent {

            return Intent(activity, ChangePasswordActivity::class.java)
        }
    }

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@ChangePasswordActivity
            changePasswordViewModel = viewModel
        }

        ProgressLoader(this, viewModel)
        AlertDialog(this, viewModel)
        viewModel?.apply {
            isChangePasswordSuccessfully.observe(this@ChangePasswordActivity, Observer {
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