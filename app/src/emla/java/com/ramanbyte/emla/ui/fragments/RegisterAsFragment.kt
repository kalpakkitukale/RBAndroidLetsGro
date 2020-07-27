package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentRegisterAsBinding
import com.ramanbyte.emla.view_model.CreateAccountViewModel
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

class RegisterAsFragment : BaseFragment<FragmentRegisterAsBinding,LoginViewModel>() {

    var mContext : Context? = null

    override val viewModelClass: Class<LoginViewModel> = LoginViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_register_as

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@RegisterAsFragment
            loginViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            viewModel.apply {

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}