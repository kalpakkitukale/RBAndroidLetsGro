package com.ramanbyte.emla.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.ramanbyte.AppController
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityLauncherBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.LauncherViewModel
import com.ramanbyte.utilities.KEY_Y
import com.ramanbyte.utilities.makeStatusBarTransparent

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LauncherActivity :
    BaseActivity<ActivityLauncherBinding, LauncherViewModel>(authModuleDependency) {

    override val viewModelClass: Class<LauncherViewModel> get() = LauncherViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_launcher

    var isUserLoggedIn: Boolean = false

    override fun initiate() {

        AppS3Client.createInstance(applicationContext).setDefaultObject("prod")

        makeStatusBarTransparent()
        setViewModelOps()
        Handler().postDelayed({
            if (isUserLoggedIn) {
                startActivity(ContainerActivity.intent(this@LauncherActivity))
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

                }
            })
        }
    }
}
