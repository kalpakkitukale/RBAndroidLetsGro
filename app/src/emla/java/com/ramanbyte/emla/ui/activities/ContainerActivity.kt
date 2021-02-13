package com.ramanbyte.emla.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.webkit.WebViewCompat
import com.google.android.material.internal.NavigationMenuView
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityContainerBinding
import com.ramanbyte.databinding.NavHeaderMainBinding
import com.ramanbyte.emla.adapters.NavigationDrawerExpandableListAdapter
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.MenuPojo
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_COURSE_MODEL
import kotlinx.android.synthetic.emla.activity_container.*


class ContainerActivity : BaseActivity<ActivityContainerBinding, ContainerViewModel>(
    authModuleDependency
) {
    private var navigationDrawerExpandableListAdapter: NavigationDrawerExpandableListAdapter? = null
    private var childList = HashMap<MenuPojo, ArrayList<MenuPojo>>()
    private var headerList = ArrayList<MenuPojo>()
    private var headerMainBinding: NavHeaderMainBinding? = null
    private var clickedNavItem: Int = 0X1
    private var isDrawerItemClicked = false
    private var selectedView: View? = null
    private var lastView: View? = null
    private var actionBar: ActionBar? = null

    override val viewModelClass: Class<ContainerViewModel> get() = ContainerViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_container

    override fun initiate() {

        AlertDialog(this, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@ContainerActivity
            containerViewModel = viewModel

            setSupportActionBar(mainToolbar)
            actionBar = supportActionBar
            setUpNavigationHeader()
            prepareStudentMenu()

            setupActionBarWithNavController(
                navController,
                appBarConfiguration
            )

            setMenuToNavigationDrawer()
            /** Handle nav drawer item clicks and navigation backpress handle
             * */
            layoutBinding.navView.setNavigationItemSelectedListener { menuItem ->
                //menuItem.isChecked = true
                drawer_layout.closeDrawers()
                true
            }
            layoutBinding.navView.setupWithNavController(navController)

            //get model data from intent and Assign to CourseDetailsFragment
            if (intent.hasExtra(KEY_COURSE_MODEL)) {
                val intentData: CoursesModel =
                    intent.getParcelableExtra(KEY_COURSE_MODEL) as CoursesModel
                navController.navigate(R.id.courseDetailFragment,
                    Bundle().apply {
                        putParcelable(KEY_COURSE_MODEL, intentData)
                    })
            }
        }

        //-------------------------- Drawer -------------------------

        headerMainBinding?.apply {
            containerViewModel = viewModel

            ivDrawerClose.setOnClickListener {
                isDrawerItemClicked = true
                layoutBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        layoutBinding.navigationListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            val menuPojo = headerList[groupPosition]
            if (!menuPojo.hasChildren) {
                clickedNavItem = menuPojo.menuId
                isDrawerItemClicked = true
                v.isSelected = true
                selectedView = v
                layoutBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            false
        }
        layoutBinding.navigationListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            if (childList[headerList[groupPosition]] != null) {
                val menuPojo = childList[headerList[groupPosition]]!![childPosition]
                clickedNavItem = menuPojo.menuId
                isDrawerItemClicked = true
                v.isSelected = true
                selectedView = v
                layoutBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            false
        }

        layoutBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(drawerViewPosition: Int) {
            }

            override fun onDrawerSlide(drawerView: View, drawerViewPosition: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                if (isDrawerItemClicked && (selectedView != null)) {
                    setMenuClickEvents(
                        clickedNavItem,
                        selectedView
                    )
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                layoutBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            }
        })

        checkSystemWebViewVersion()
    }

    /**Lazy init Navigation host controller*/
    private val navController by lazy { findNavController(R.id.containerNavHost) }

    /**Here add the navigation drawer fragments to show Menu(Hamburger) icon
     * This appBarConfiguration is used to handle the menu icon and back arrow*/
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.coursesFragment,
                R.id.myDownloadsFragment,
                R.id.myFavouriteVideoFragment,
                R.id.settingFragment
            ), layoutBinding.drawerLayout
        )
    }

    companion object {
        private val NAV_COURSES = 0x1
        private val NAV_DOWNLOADS = 0x2
        private val NAV_MY_FAVOURATE = 0x3
        private val NAV_MY_CART = 0x4
        private val NAV_MY_COURSE = 0x5
        private val NAV_SETTINGS = 0x6

        fun intent(activity: Activity): Intent {
            return Intent(activity, ContainerActivity::class.java)
        }
    }

    override fun onBackPressed() {

        when {
            layoutBinding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                layoutBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
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
    }

    /**
     * onSupportNavigateUp used to handle backpress for fragments other than the navigation menus*/
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**Set up navigation drawer header layout*/
    private fun setUpNavigationHeader() {
        disableNavigationViewScrollbars()
        val layoutParams = layoutBinding.navView.layoutParams as DrawerLayout.LayoutParams
        val displayMetrics = resources.displayMetrics
        layoutParams.width = 75 * displayMetrics.widthPixels / 100
        layoutBinding.navView.layoutParams = layoutParams
        headerMainBinding = NavHeaderMainBinding.bind(layoutBinding.navView.getHeaderView(0))
    }

    /**Setting up the navigation expandable list*/
    private fun setMenuToNavigationDrawer() {
        try {
            layoutBinding.navigationListView.setGroupIndicator(null)
            layoutBinding.navigationListView.setChildIndicator(null)
            navigationDrawerExpandableListAdapter =
                NavigationDrawerExpandableListAdapter(this, headerList, childList)
            layoutBinding.navigationListView.setAdapter(navigationDrawerExpandableListAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun disableNavigationViewScrollbars() {
        if (layoutBinding.navView != null) {
            val navigationMenuView: NavigationMenuView? =
                layoutBinding.navView.getChildAt(0) as NavigationMenuView
            if (navigationMenuView != null) {
                navigationMenuView.isVerticalScrollBarEnabled = false
            }
        }
    }

    private fun prepareStudentMenu() {
        try {
            headerList = ArrayList()
            childList = java.util.HashMap()

            var menuPojo =
                MenuPojo(
                    NAV_COURSES,
                    R.drawable.ic_open_book,
                    BindingUtils.string(R.string.course),
                    false,
                    0
                )
            headerList.add(menuPojo)

            menuPojo = MenuPojo(
                NAV_DOWNLOADS,
                R.drawable.ic_download,
                BindingUtils.string(R.string.my_downloads),
                false,
                0
            )
            headerList.add(menuPojo)


            menuPojo = MenuPojo(
                NAV_MY_FAVOURATE,
                R.drawable.ic_heart,
                BindingUtils.string(R.string.my_favourite),
                false,
                0
            )
            headerList.add(menuPojo)

            menuPojo = MenuPojo(
                NAV_MY_CART,
                R.drawable.ic_my_cart,
                BindingUtils.string(R.string.my_cart),
                false,
                0
            )
            headerList.add(menuPojo)

            menuPojo = MenuPojo(
                NAV_MY_COURSE,
                R.drawable.ic_mycourses,
                BindingUtils.string(R.string.my_course),
                false,
                0
            )
            headerList.add(menuPojo)


            menuPojo = MenuPojo(
                NAV_SETTINGS,
                R.drawable.ic_settings,
                BindingUtils.string(R.string.settings),
                false,
                0
            )
            headerList.add(menuPojo)

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun setMenuClickEvents(menuId: Int, view: View?) {
        try {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.coursesFragment, false).build()
            when (menuId) {
                NAV_COURSES -> {
                    if (navController.currentDestination?.id != R.id.coursesFragment)
                        navController.navigate(R.id.coursesFragment, null, navOption)
                }
                NAV_DOWNLOADS -> {
                    if (navController.currentDestination?.id != R.id.myDownloadsFragment)
                        navController.navigate(R.id.myDownloadsFragment, null, navOption)
                }
                NAV_MY_FAVOURATE -> {
                    if (navController.currentDestination?.id != R.id.myFavouriteVideoFragment)
                        navController.navigate(R.id.myFavouriteVideoFragment, null, navOption)
                }
                NAV_MY_CART -> {
                    if (navController.currentDestination?.id != R.id.cartFragment)
                        navController.navigate(R.id.cartFragment, null, navOption)
                }
                NAV_MY_COURSE -> {
                    if (navController.currentDestination?.id != R.id.myCourseFragment)
                        navController.navigate(R.id.myCourseFragment, null, navOption)
                }
                NAV_SETTINGS -> {
                    if (navController.currentDestination?.id != R.id.settingFragment)
                        navController.navigate(R.id.settingFragment, null, navOption)
                }
            }

            if (view != null) {
                if (lastView != null) {
                    lastView?.setBackgroundColor(Color.TRANSPARENT)
                    lastView = view
                } else lastView = view
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        } finally {
            //isDrawerItemClicked = false
            layoutBinding.drawerLayout.closeDrawer(GravityCompat.START)
            isDrawerItemClicked = false
        }
    }

    private fun checkSystemWebViewVersion() {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this)

            val webViewPackageInfoStr = webViewPackageInfo?.versionName.toString()
            val convert = webViewPackageInfoStr.split("\\.".toRegex()).toTypedArray()
            val webViewVersion = convert[0].toInt()
            AppLog.infoLog("webViewVersion :: $webViewVersion")

            if (webViewVersion < 83) {
                viewModel.apply {
                    setAlertDialogResourceModelMutableLiveData(
                        message = BindingUtils.string(R.string.webview_update_message),
                        alertDrawableResource = BindingUtils.drawable(R.drawable.ic_warning),
                        isInfoAlert = false,
                        positiveButtonText = BindingUtils.string(R.string.str_update),
                        negativeButtonText = BindingUtils.string(R.string.strCancel),
                        positiveButtonClickFunctionality = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data =
                                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.webview")
                            startActivity(intent)

                        }, negativeButtonClickFunctionality = {
                            isAlertDialogShown.postValue(false)

                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
            }
        }

    }

}