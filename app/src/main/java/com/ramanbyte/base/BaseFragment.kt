package com.ramanbyte.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ramanbyte.databinding.CompanyListFragmentBinding
import com.ramanbyte.databinding.FragmentDashboardBinding
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

        if (::layoutBinding.isInitialized)
            return layoutBinding.root

        layoutBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)

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

    private fun toggleBottomBar() {

        activity?.run {

            val containerViewModel = ViewModelProvider(this).get(ContainerViewModel::class.java)

            containerViewModel.bottomBarVisibilityLiveData.value =
                if (layoutBinding is FragmentDashboardBinding || layoutBinding is CompanyListFragmentBinding) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    var mToolbar: Toolbar? = null
    fun setToolbar(toolbarTitle: String, toolbar: Toolbar, homeAsUpEnabled: Boolean = true) {
        mToolbar = toolbar
//        (activity as DashboardActivity?)?.apply {
//            setSupportActionBar(mToolbar.apply {
//                title = toolbarTitle
//            })
//            supportActionBar?.apply { setDisplayHomeAsUpEnabled(homeAsUpEnabled) }

            /*if (!homeAsUpEnabled) {

                val appBarConfiguration = AppBarConfiguration(
                    topLevelDestinationIds = setOf(
                        R.id.dashboardFragment,
                        R.id.companyListFragment
                    )
                )

                setupActionBarWithNavController(
                    findNavController(R.id.containerNavHost),
                    appBarConfiguration
                )

            }*/
//        }
    }

    fun setToolbarTitle(strTitle: String) {
        (activity as ContainerActivity?)?.apply {
            supportActionBar?.apply {
                title = strTitle
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //toggleBottomBar()
    }

    fun initializeContainerViewModel() {

        activity?.run {

            containerViewModel = ViewModelProvider(this).get(ContainerViewModel::class.java)
        }

    }

    abstract val viewModelClass: Class<VM>

    @LayoutRes
    protected abstract fun layoutId(): Int

    abstract fun initiate()
}