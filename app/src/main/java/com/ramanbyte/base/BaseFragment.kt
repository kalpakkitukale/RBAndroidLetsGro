package com.ramanbyte.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.emla.view_model.ContainerViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

abstract class BaseFragment<LayoutBinding : ViewDataBinding, VM : ViewModel>(
    private val isActivityParent: Boolean = false,
    private val useParent: Boolean = false,
    private val isNestedGraph: Boolean = false
) : Fragment(),
    KodeinAware {

    override val kodein: Kodein by kodein()

    protected lateinit var layoutBinding: LayoutBinding
    lateinit var viewModel: VM

    var containerViewModel: ContainerViewModel? = null

    private val viewModelFactory: ViewModelProvider.Factory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!(::layoutBinding.isInitialized))
            layoutBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        else
            container?.removeView(layoutBinding.root)

        //bindContentView(inflater, layoutId(), container)

        //  context ?: return layoutBinding.root

        setHasOptionsMenu(true)

        subscribeUi()

        initiate()

        return layoutBinding.root
    }

    private fun subscribeUi() {
        if (useParent) {
            when {
                isActivityParent -> {
                    activity?.apply {
                        viewModel = ViewModelProvider(this).get(viewModelClass)
                    }
                }
                isNestedGraph -> {
                    viewModel =
                        ViewModelProvider(requireParentFragment().requireParentFragment()).get(
                            viewModelClass
                        )
                }
                else -> {
                    viewModel = ViewModelProvider(requireParentFragment()).get(viewModelClass)
                }
            }

        } else {
            viewModel = ViewModelProvider(this, viewModelFactory).get(viewModelClass)
        }
    }

    fun setToolbarTitle(strTitle: String) {
        (activity as ContainerActivity?)?.apply {
            supportActionBar?.apply {
                title = strTitle
            }
        }
    }

    fun initializeContainerViewModel() {

        activity?.run {

            containerViewModel = ViewModelProvider(this).get(ContainerViewModel::class.java)
        }

    }

    abstract val viewModelClass: Class<VM>

    @LayoutRes
    protected abstract fun layoutId(): Int

    protected abstract fun initiate()
}