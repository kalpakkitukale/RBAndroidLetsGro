package com.ramanbyte.emla.faculty.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentFacultySettingBinding
import com.ramanbyte.emla.ui.activities.LoginActivity
import com.ramanbyte.emla.view_model.SettingViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * A simple [Fragment] subclass.
 */
class FacultySettingFragment : BaseFragment<FragmentFacultySettingBinding,SettingViewModel>() {

    override val viewModelClass: Class<SettingViewModel> = SettingViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_faculty_setting

    override fun initiate() {
        layoutBinding.apply {
            settingViewModel = viewModel
            AlertDialog(context!!, viewModel)
            ProgressLoader(context!!, viewModel)

            setListener()
        }
    }

    private fun setListener() {
        viewModel?.apply {
            clickOnLogoutLiveData?.observe(this@FacultySettingFragment, Observer { click ->
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
