package com.ramanbyte.emla.faculty.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityFacultyContainerBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.faculty.view_model.FacultyContainerViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_Y
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.changeStatusBarColor

class FacultyContainerActivity : BaseActivity<ActivityFacultyContainerBinding,FacultyContainerViewModel>(
    authModuleDependency
) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    var isUserLoggedIn: Boolean = false

    override val viewModelClass: Class<FacultyContainerViewModel> = FacultyContainerViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_faculty_container

    override fun initiate() {

        AlertDialog(this, viewModel)

        changeStatusBarColor(window, R.color.colorPrimaryDark)
        layoutBinding.apply {
            lifecycleOwner = this@FacultyContainerActivity
            facultyContainerViewModel = viewModel

            navController = findNavController(R.id.facultyContainerNavHost)
            appBarConfiguration = AppBarConfiguration.Builder(
                R.id.facultyCoursesFragment,
                R.id.facultySettingFragment
            ).build()
            setSupportActionBar(mainToolbar)
            setupActionBarWithNavController(
                navController,
                appBarConfiguration
            )
            visibilityNavElements(navController)

            viewModel.apply {
                /*userModelLiveData.observe(this@FacultyContainerActivity, Observer {
                    it?.let {
                        isUserLoggedIn = it.loggedId == KEY_Y
                        AppLog.infoLog("isUserLoggedIjjjj $isUserLoggedIn")

                    }
                    //AppLog.infoLog("isUserLoggedIjjjj ${it.userType}")
                })*/
            }
        }
    }

    private fun visibilityNavElements(navController: NavController) {

        //Listen for the change in fragment (navigation) and hide or show drawer or bottom navigation accordingly if required
        //Modify this according to your need
        //If you want you can implement logic to deselect(styling) the bottom navigation menu item when drawer item selected using listener

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.facultyCoursesFragment,
                R.id.facultySettingFragment -> showBottomNavigation()//show bottom nav on these fragments only
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
            return Intent(activity, FacultyContainerActivity::class.java)
        }
    }
}
