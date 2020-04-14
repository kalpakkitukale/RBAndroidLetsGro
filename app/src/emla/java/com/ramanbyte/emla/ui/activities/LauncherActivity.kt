package com.ramanbyte.emla.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.ramanbyte.R
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

class LauncherActivity : BaseActivity<ActivityLauncherBinding, LauncherViewModel>(authModuleDependency) {

    override val viewModelClass: Class<LauncherViewModel> get() = LauncherViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_launcher

    override fun initiate() {
        makeStatusBarTransparent()
        setViewModelOps()
    }

    private fun setViewModelOps() {
        viewModel.apply {
            userModelLiveData.observe(this@LauncherActivity, Observer {
                it?.let {
                    Handler().postDelayed({
                        if (it.loggedId == KEY_Y) {
                            startActivity(ContainerActivity.intent(this@LauncherActivity))
                        } else {
                            startActivity(LoginActivity.intent(this@LauncherActivity))
                        }
                        finish()
                    }, 1500)
                }
            })
        }
    }
}
