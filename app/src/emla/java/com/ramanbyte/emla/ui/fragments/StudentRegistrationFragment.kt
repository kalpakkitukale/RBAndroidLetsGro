package com.ramanbyte.emla.ui.fragments

import android.content.Context
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentStudentRegistrationBinding
import com.ramanbyte.emla.view_model.CreateAccountViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

class StudentRegistrationFragment :
    BaseFragment<FragmentStudentRegistrationBinding, CreateAccountViewModel>() {

    var mContext: Context? = null

    override val viewModelClass: Class<CreateAccountViewModel> = CreateAccountViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_student_registration

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@StudentRegistrationFragment
            createAccountViewModel = viewModel
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