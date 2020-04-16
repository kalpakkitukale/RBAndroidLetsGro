package com.ramanbyte.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseParentFragment<LayoutBinding : ViewDataBinding, VM : ViewModel, ParentVM : BaseViewModel>(
    isActivityParent: Boolean = false,
    useParent: Boolean = false,
    isNestedGraph: Boolean = false
) : BaseFragment<LayoutBinding, VM>(isActivityParent, useParent, isNestedGraph) {

    lateinit var parentViewModel: ParentVM

    abstract val parentViewModelClass: Class<ParentVM>

    override fun initiate() {
        requireParentFragment().run {
            parentViewModel = ViewModelProvider(this).get(parentViewModelClass)
        }

        initiateView()
    }

    abstract fun initiateView()
}