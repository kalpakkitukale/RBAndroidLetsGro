package com.ramanbyte.emla.ui.fragments

import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentSettingBinding
import com.ramanbyte.emla.view_model.SettingViewModel

/**
 * @author Shital K
 */
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    override val viewModelClass: Class<SettingViewModel> = SettingViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_setting

    override fun initiate() {
        layoutBinding.apply {
            settingviewModel = viewModel
        }
    }
}
