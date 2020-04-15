package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityContainerBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.ui.fragments.AllTheBestFragment
import com.ramanbyte.emla.ui.fragments.QuizInstructionFragment
import com.ramanbyte.emla.ui.fragments.ShowQuestionFragment
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.changeStatusBarColor
import com.ramanbyte.utilities.changeStatusBarColor
import com.ramanbyte.utilities.makeStatusBarTransparent


class ContainerActivity : BaseActivity<ActivityContainerBinding, ContainerViewModel>(
    authModuleDependency
) {

    override val viewModelClass: Class<ContainerViewModel> get() = ContainerViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_container

    override fun initiate() {
        changeStatusBarColor(window, R.color.colorPrimaryDark)
        layoutBinding.apply {
            setSupportActionBar(mainToolbar)
        }
        setBottomNavigation()

    }

    private fun setBottomNavigation() {

        val navController = findNavController(R.id.containerNavHost)
        layoutBinding.apply {
            bottomNavigationView.setupWithNavController(navController)

            val appBarConfiguration = AppBarConfiguration(
                topLevelDestinationIds = setOf(
                    R.id.coursesFragment,
                    R.id.myDownloadsFragment,
                    R.id.learnerProfileFragment
                )
            )
            // Setting Up ActionBar with Navigation Controller
            setupActionBarWithNavController(navController,appBarConfiguration)
        }
    }


    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, ContainerActivity::class.java)
        }
    }

    override fun onBackPressed() {
        //viewModel.alertInterruptDialog()
        //super.onBackPressed()

        val fragment = supportFragmentManager.findFragmentByTag(BindingUtils.string(R.string.show_question)) as ShowQuestionFragment?  // ShowQuestionFragment

        if (fragment != null) {
            viewModel.apply {

                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.leave_test_message),
                    BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                    false,
                    BindingUtils.string(R.string.yes), {
                        isAlertDialogShown.postValue(false)
                        super.onBackPressed()
                    },
                    BindingUtils.string(R.string.no), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            }
        }else {
            super.onBackPressed()
        }
    }


}
