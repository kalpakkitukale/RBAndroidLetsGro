package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityContainerBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_COURSE_MODEL
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.changeStatusBarColor
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf


class ContainerActivity : BaseActivity<ActivityContainerBinding, ContainerViewModel>(
    authModuleDependency
) {

    override val viewModelClass: Class<ContainerViewModel> get() = ContainerViewModel::class.java
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    override fun layoutId(): Int = R.layout.activity_container

    override fun initiate() {


        AlertDialog(this, viewModel)

        changeStatusBarColor(window, R.color.colorPrimaryDark)
        layoutBinding.apply {
            lifecycleOwner = this@ContainerActivity

            containerViewModel = viewModel

            navController = findNavController(R.id.containerNavHost)
            appBarConfiguration = AppBarConfiguration.Builder(
                R.id.coursesFragment, R.id.myDownloadsFragment, R.id.myFavouriteVideoFragment,
                R.id.settingFragment
            ).build()
            setSupportActionBar(mainToolbar)
            setupActionBarWithNavController(
                navController,
                appBarConfiguration
            )
            visibilityNavElements(navController)

        //get model data from intent and Assign to CourseDetailsFragment
            if (intent.hasExtra(KEY_COURSE_MODEL)) {
                val intentData  : CoursesModel = intent.getParcelableExtra(KEY_COURSE_MODEL) as CoursesModel
                navController.navigate(R.id.courseDetailFragment,
                    Bundle().apply { putParcelable(KEY_COURSE_MODEL, intentData)
                    })
            }
        }
    }

    private fun visibilityNavElements(navController: NavController) {

        //Listen for the change in fragment (navigation) and hide or show drawer or bottom navigation accordingly if required
        //Modify this according to your need
        //If you want you can implement logic to deselect(styling) the bottom navigation menu item when drawer item selected using listener

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.coursesFragment,
                R.id.myDownloadsFragment, R.id.myFavouriteVideoFragment,
                R.id.settingFragment -> showBottomNavigation()//show bottom nav on these fragments only
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
        if (navController.currentDestination?.id == R.id.coursesFragment) {
            moveTaskToBack(true)
            finish()
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

        } else {
            super.onBackPressed()
        }
    }
}
