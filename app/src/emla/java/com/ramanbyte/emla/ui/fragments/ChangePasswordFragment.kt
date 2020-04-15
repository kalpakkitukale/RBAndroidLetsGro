package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.ChangePasswordBinding
import com.ramanbyte.emla.view_model.ChangePasswordViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

class ChangePasswordFragment : BaseFragment<ChangePasswordBinding, ChangePasswordViewModel>() {
    override val viewModelClass: Class<ChangePasswordViewModel> =
        ChangePasswordViewModel::class.java

    override fun layoutId(): Int = R.layout.change_password
    private var mContext: Context? = null

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@ChangePasswordFragment
            changePasswordViewModel = viewModel
        }
        setHasOptionsMenu(true)
        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)
        viewModel?.apply {

            isChangePasswordSuccessfully.observe(this@ChangePasswordFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        isChangePasswordSuccessfully.value = null
                        findNavController().navigateUp()
                    }
                }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


