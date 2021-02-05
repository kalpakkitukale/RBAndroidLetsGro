package com.ramanbyte.emla.ui.activities

import android.view.MenuItem
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityJoinClassroomBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.JoinClassroomViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.FileUtils

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
            lifecycleOwner =this@JoinClassroomActivity
            setUpToolBar()
            try {
                val intendData = intent.extras
                if (intendData?.getBoolean("fromClassroomPlus") == true) {
                    layoutBinding.tvUserId.text = "classroom userID : ${intendData.getInt("user_id")}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message,e)
            }
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(layoutBinding.appBarLayout.toolbar)
        supportActionBar?.also {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
        layoutBinding.appBarLayout.title = BindingUtils.string(R.string.join_with_classroom)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                return true
            }
            else->{
                return super.onOptionsItemSelected(item)
            }
        }

    }
}