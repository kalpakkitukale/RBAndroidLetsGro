package com.ramanbyte.emla.ui.activities

import android.content.Intent
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityJoinClassroomBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.faculty.ui.activities.FacultyContainerActivity
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.JoinClassroomViewModel
import com.ramanbyte.utilities.*
import kotlin.system.exitProcess

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 04/02/2021
 */
class JoinClassroomActivity : BaseActivity<ActivityJoinClassroomBinding, JoinClassroomViewModel>(
    authModuleDependency
) {

    override val viewModelClass: Class<JoinClassroomViewModel>
        get() = JoinClassroomViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_join_classroom

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@JoinClassroomActivity
            AlertDialog(this@JoinClassroomActivity, viewModel)
            ProgressLoader(this@JoinClassroomActivity, viewModel)
            joinClassroomViewModel = viewModel

            try {
                val intendData = intent.extras
                var title = BindingUtils.string(R.string.join_with_classroom)
                if (intendData?.getBoolean("fromClassroomPlus") == true) {
                    mainContainer.visibility = View.GONE
                    viewModel.classroom_user_ref_id = intendData.getInt("user_id")
                    AppLog.infoLog("classroom userID : ${intendData.getInt("user_id")}")
                } else {
                    mainContainer.visibility = View.VISIBLE
                    title = BindingUtils.string(R.string.log_in_with_classroom)
                }
                setUpToolBar(title)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }
            setListeners()
            setObservers()
        }
    }

    private fun setListeners() {
        layoutBinding.apply {
            rgHaveAccount.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbExistingYes -> {
                        groupClassroomLogin.visibility = View.VISIBLE
                        groupCreateAccount.visibility = View.GONE
                    }
                    R.id.rbExistingNo -> {
                        groupClassroomLogin.visibility = View.GONE
                        groupCreateAccount.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.apply {
            isUserLoggedIn()
            userModelLiveData.observe(this@JoinClassroomActivity, Observer {
                it?.let {
                    val currentUser = it.userType
                    if (it.loggedId == KEY_Y) {
                        if (currentUser == KEY_STUDENT) {
                            //get data from intent filter
                            startActivity(handleIntent())
                        } else
                            startActivity(FacultyContainerActivity.intent(this@JoinClassroomActivity))

                    } else {
                        if (currentUser == KEY_FACULTY)
                            startActivity(FacultyContainerActivity.intent(this@JoinClassroomActivity))
                        else
                            startActivity(LoginActivity.intent(this@JoinClassroomActivity))
                    }
                    finish()
                    BaseAppController.setEnterPageAnimation(this@JoinClassroomActivity)
                    userModelLiveData.value = null
                }
            })
            exitApplicationLiveData.observe(this@JoinClassroomActivity, Observer {
                it?.let {
                    exitApplicationLiveData.value = null
                    AppLog.infoLog("user different EXIT application")
                    exitProcess(0)
                }
            })
        }
    }

    private fun handleIntent(): Intent {
        val launcherIntent = Intent(this@JoinClassroomActivity, ContainerActivity::class.java)
        val appLinkIntent = intent
        val appLinkData = appLinkIntent.data
        return if (appLinkData != null) {
            val data: MutableList<String>? = appLinkData.pathSegments

            val data1 = data?.joinToString()
            var result = data1?.split("=", ",")?.map { it.trim() }

            var result1 = result?.drop(1)
            var result2 = result1?.drop(1)

            var courseid = result2?.get(0)
            var courseName = result2?.get(1)
            var courseDescription = result2?.get(2)
            var courseCode = result2?.get(3)
            var image = result2?.get(4)
            var imageurl = result2?.get(5)
            var imageUrl = image + "/" + imageurl

            var totalCount = result2?.get(6)

            var courseM: CoursesModel = CoursesModel()

            courseM.apply {
                this.courseId = courseid?.toInt()!!
                this.courseName = courseName
                this.courseDescription = courseDescription
                this.courseCode = courseCode
                this.courseImage = imageUrl
                this.totalCount = totalCount?.toInt()!!

            }
            launcherIntent.putExtra(KEY_COURSE_MODEL, courseM as Parcelable)
        } else {
            launcherIntent
        }

    }

    private fun setUpToolBar(toolbarTitle: String = BindingUtils.string(R.string.join_with_classroom)) {
        setSupportActionBar(layoutBinding.appBarLayout.toolbar)
        supportActionBar?.also {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
        layoutBinding.appBarLayout.title = toolbarTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        /*if (intent?.extras?.getBoolean("fromClassroomPlus") == true) {
            val intent =
                Intent(this@JoinClassroomActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            startActivity(intent)
            AppController.setExitPageAnimation(this@JoinClassroomActivity)
            finish()
        } else*/ super.onBackPressed()
    }
}