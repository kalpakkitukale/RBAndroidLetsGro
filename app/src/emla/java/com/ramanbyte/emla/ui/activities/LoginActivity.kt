package com.ramanbyte.emla.ui.activities


import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityLoginBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.utilities.*

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(authModuleDependency) {

    private lateinit var navController: NavController

    override val viewModelClass: Class<LoginViewModel> = LoginViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_login

    override fun initiate() {
        makeStatusBarTransparent()
        layoutBinding.apply {
            lifecycleOwner = this@LoginActivity
            loginViewModel = viewModel
            ProgressLoader(this@LoginActivity, viewModel)
            AlertDialog(this@LoginActivity, viewModel)

            navController = findNavController(R.id.loginNavHost)

            setSupportActionBar(mainToolbar)

            actionBar?.hide()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, LoginActivity::class.java)
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.facultyRegistrationFragment || navController.currentDestination?.id == R.id.studentRegistrationFragment) {
            viewModel.setToolbarTitle(View.GONE, KEY_BLANK)
            super.onBackPressed()
        } else if (navController.currentDestination?.id == R.id.learnerProfileFragment) {
            viewModel.apply {
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.profile_changes_discarded),
                    BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                    false,
                    BindingUtils.string(R.string.yes), {
                        isAlertDialogShown.postValue(false)
                        navController.navigateUp()
                    },
                    BindingUtils.string(R.string.no), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            }
        }else{
            super.onBackPressed()
        }

    }
}