package com.ramanbyte.emla.ui.activities


import android.content.Intent
import android.os.Handler
import android.os.Parcelable
import androidx.lifecycle.Observer
import com.ramanbyte.BaseAppController
import com.ramanbyte.BuildConfig
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityLauncherBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.faculty.ui.activities.FacultyContainerActivity
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.LauncherViewModel
import com.ramanbyte.utilities.*


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LauncherActivity :
    BaseActivity<ActivityLauncherBinding, LauncherViewModel>(authModuleDependency) {

    override val viewModelClass: Class<LauncherViewModel> get() = LauncherViewModel::class.java
    override fun layoutId(): Int = R.layout.activity_launcher

    var isUserLoggedIn: Boolean = false
    var currentUser: String = KEY_BLANK

    override fun initiate() {
        AppS3Client.createInstance(applicationContext).setDefaultObject(BuildConfig.S3_OBJECT)

        makeStatusBarTransparent()
        setViewModelOps()

        Handler().postDelayed({
            if (isUserLoggedIn) {
                if (currentUser == KEY_STUDENT) {
                    //get data from intent filter
                    startActivity(handleIntent())
                }
                else
                    startActivity(FacultyContainerActivity.intent(this@LauncherActivity))
            } else {
                startActivity(LoginActivity.intent(this@LauncherActivity))
            }
            finish()
            BaseAppController.setEnterPageAnimation(this@LauncherActivity)
        }, 1500)
    }
    private fun setViewModelOps() {
        viewModel.apply {
            userModelLiveData.observe(this@LauncherActivity, Observer {
                it?.let {
                    isUserLoggedIn = it.loggedId == KEY_Y
                    currentUser = it.userType
                }
            })
        }
    }
    //get data from intent and covert into viewModel and share Using intent to ContainerActivity
fun handleIntent():Intent{
    val launcherIntent = Intent(this@LauncherActivity,ContainerActivity::class.java)
        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
        return if(appLinkData!=null){
            val data: MutableList<String>? =appLinkData.pathSegments

            val data1 =data?.joinToString()
            var result = data1?.split("=", ",")?.map { it.trim() }

            var result1=result?.drop(1)
            var result2=result1?.drop(1)

            var courseid= result2?.get(0)
            var courseName= result2?.get(1)
            var courseDescription=result2?.get(2)
            var courseCode=result2?.get(3)
            var image=result2?.get(4)
            var imageurl=result2?.get(5)
            var imageUrl=image+"/"+imageurl

            var totalCount=result2?.get(6)

            var courseM: CoursesModel= CoursesModel()

            courseM.apply {
                this.courseId= courseid?.toInt()!!
                this.courseName=courseName
                this.courseDescription=courseDescription
                this.courseCode=courseCode
                this.courseImage=imageUrl
                this.totalCount=totalCount?.toInt()!!

            }
            launcherIntent.putExtra(KEY_COURSE_MODEL, courseM as Parcelable)
        }else{
            launcherIntent
        }

    }

}
