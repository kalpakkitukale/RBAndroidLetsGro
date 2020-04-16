package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
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
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    override fun layoutId(): Int = R.layout.activity_container

    override fun initiate() {
        changeStatusBarColor(window, R.color.colorPrimaryDark)
        layoutBinding.apply {
            lifecycleOwner = this@ContainerActivity

            containerViewModel = viewModel

            navController = findNavController(R.id.containerNavHost)
            appBarConfiguration = AppBarConfiguration.Builder(
                R.id.coursesFragment, R.id.myDownloadsFragment,
                R.id.learnerProfileFragment
            ).build()
            setSupportActionBar(mainToolbar)
            setupActionBarWithNavController(
                navController,
                appBarConfiguration
            )
            visibilityNavElements(navController)
        }

    }

    private fun visibilityNavElements(navController: NavController) {

        //Listen for the change in fragment (navigation) and hide or show drawer or bottom navigation accordingly if required
        //Modify this according to your need
        //If you want you can implement logic to deselect(styling) the bottom navigation menu item when drawer item selected using listener

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.coursesFragment,
                R.id.myDownloadsFragment,
                R.id.learnerProfileFragment -> showBottomNavigation()//show bottom nav on these fragments only
                else -> hideBottomNavigation()//hide bottom navigation
            }
        }
    }

    private fun hideBottomNavigation() { //Hide both drawer and bottom navigation bar
        layoutBinding.apply {
            bottomNavigationView.visibility = View.GONE
        }
    }

    private fun showBottomNavigation() {
        layoutBinding.apply {
            bottomNavigationView.visibility = View.VISIBLE
            setupNavControl() //To configure navController with drawer and bottom navigation
        }
    }

    private fun setupNavControl() {
        layoutBinding.apply {
            bottomNavigationView.setupWithNavController(navController) //Setup Bottom navigation with navController
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

        val fragment =
            supportFragmentManager.findFragmentByTag(BindingUtils.string(R.string.show_question)) as ShowQuestionFragment?  // ShowQuestionFragment

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
        } else {
            super.onBackPressed()
        }
    }


}
