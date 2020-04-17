package com.ramanbyte.emla.ui.fragments

import android.content.Intent
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentSettingBinding
import com.ramanbyte.emla.ui.activities.LoginActivity
import com.ramanbyte.emla.view_model.SettingViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * @author Shital K
 */
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    override val viewModelClass: Class<SettingViewModel> = SettingViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_setting

    override fun initiate() {
        layoutBinding.apply {
            settingviewModel = viewModel
            AlertDialog(context!!, viewModel)
            ProgressLoader(context!!, viewModel)
        }
        setListener()
    }

    private fun setListener() {
        viewModel?.apply {
            clickOnLogoutLiveData?.observe(this@SettingFragment, Observer { click ->
                if (click != null) {
                    if (click == true) {
                        val intent = Intent(context!!, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }

                        startActivity(intent)
                        clickOnLogoutLiveData?.value = false
                    }
                }
            })
        }
    }
}