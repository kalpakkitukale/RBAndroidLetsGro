package com.ramanbyte.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ramanbyte.view_model.factory.BaseViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

abstract class BaseBottomSheetFragment<layoutBinding : ViewDataBinding, VM : ViewModel>(
    private val isActivityParent: Boolean = false,
    private val useParent: Boolean = false
) : BottomSheetDialogFragment(),
    KodeinAware {
    override val kodein: Kodein by kodein()

    protected lateinit var layoutBinding: layoutBinding
    lateinit var viewModel: VM

    private val viewModelFactory: BaseViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(DialogFragment.STYLE_NO_FRAME, 0)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val windowParams = window!!.attributes
        windowParams.dimAmount = 0.70f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindContentView(inflater, layoutId(), container)
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initiate()
    }

    private fun bindContentView(
        inflater: LayoutInflater,
        layoutId: Int,
        container: ViewGroup?
    ) {
        try {

            layoutBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)

            /*given line is for separate view model for each*/
            //viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
            /* given logic is for shared view model */
            if (useParent) {
                if (isActivityParent) {
                    activity?.apply {
                        viewModel = ViewModelProvider(this).get(viewModelClass)
                    }
                } else {
                    viewModel = ViewModelProvider(requireParentFragment()).get(viewModelClass)
                }

            } else {
                viewModel = ViewModelProvider(this, viewModelFactory).get(viewModelClass)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    abstract val viewModelClass: Class<VM>

    @LayoutRes
    protected abstract fun layoutId(): Int

    abstract fun initiate()
}