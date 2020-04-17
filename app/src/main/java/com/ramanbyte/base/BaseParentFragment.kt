package com.ramanbyte.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseParentFragment<LayoutBinding : ViewDataBinding, VM : ViewModel, ParentVM : BaseViewModel>(
    isActivityParent: Boolean = false,
    useParent: Boolean = false,
    private val isNestedGraph: Boolean = false
) : BaseFragment<LayoutBinding, VM>(isActivityParent, useParent, isNestedGraph) {

    lateinit var parentViewModel: ParentVM

    abstract val parentViewModelClass: Class<ParentVM>

    override fun initiate() {

        if (isNestedGraph) {
            requireParentFragment().requireParentFragment().run {
                parentViewModel = ViewModelProvider(this).get(parentViewModelClass)
            }
        } else {
            requireParentFragment().run {
                parentViewModel = ViewModelProvider(this).get(parentViewModelClass)
            }
        }

        initiateView()
    }

    abstract fun initiateView()
}