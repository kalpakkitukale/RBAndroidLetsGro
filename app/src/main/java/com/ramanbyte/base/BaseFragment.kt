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
import com.ramanbyte.emla.faculty.view_model.FacultyContainerViewModel
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.emla.ui.activities.LoginActivity
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.emla.view_model.LoginViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

abstract class BaseFragment<LayoutBinding : ViewDataBinding, VM : ViewModel>(
    private val isActivityParent: Boolean = false,
    private val useParent: Boolean = false,
    private val isNestedGraph: Boolean = false,
    private val hasOptionsMenu: Boolean = true
) : Fragment(),
    KodeinAware {

    override val kodein: Kodein by kodein()

    protected lateinit var layoutBinding: LayoutBinding
    lateinit var viewModel: VM

    var containerViewModel: ContainerViewModel? = null
    var facultyContainerViewModel: FacultyContainerViewModel? = null

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

        setHasOptionsMenu(hasOptionsMenu)

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
        activity?.apply {

            if (activity is ContainerActivity) {
                ViewModelProvider(this).get(ContainerViewModel::class.java).toolbarTitleLiveData.value =
                    strTitle
            } else if (activity is LoginActivity) {
                ViewModelProvider(this).get(LoginViewModel::class.java).apply {
                    setToolbarTitle(View.VISIBLE, strTitle)
                }
            } else {
                ViewModelProvider(this).get(FacultyContainerViewModel::class.java).toolbarTitleLiveData.value =
                    strTitle
            }
        }
    }

    fun initializeContainerViewModel() {

        activity?.run {
            if (activity is ContainerActivity) {
                containerViewModel = ViewModelProvider(this).get(ContainerViewModel::class.java)
            } else {
                facultyContainerViewModel =
                    ViewModelProvider(this).get(FacultyContainerViewModel::class.java)
            }

        }

    }


    abstract val viewModelClass: Class<VM>

    @LayoutRes
    protected abstract fun layoutId(): Int

    protected abstract fun initiate()

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return  when (item.itemId) {
//            android.R.id.home->{
//                findNavController().navigateUp()
//                true
//            }
//            else-> super.onOptionsItemSelected(item)
//        }
//    }

}