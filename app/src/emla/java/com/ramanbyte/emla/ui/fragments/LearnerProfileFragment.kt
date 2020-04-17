package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentLearnerProfileBinding
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.utilities.ProgressLoader

/**
 * A simple [Fragment] subclass.
 */
class LearnerProfileFragment :
    BaseFragment<FragmentLearnerProfileBinding, LearnerProfileViewModel>() {

    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_learner_profile

    override fun initiate() {

        ProgressLoader(context!!, viewModel)

        layoutBinding.apply {

            lifecycleOwner = this@LearnerProfileFragment
        }
    }

}
