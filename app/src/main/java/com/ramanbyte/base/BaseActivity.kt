package com.ramanbyte.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ramanbyte.R
import com.ramanbyte.utilities.DI_ACTIVITY_CONTEXT
import com.ramanbyte.utilities.changeStatusBarColor
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

abstract class BaseActivity<LayoutBinding : ViewDataBinding, VM : ViewModel>(
    private val moduleDependency: Kodein.Module,
    private val isLandscape: Boolean = false
) :
    AppCompatActivity(), KodeinAware {

    val _parentKodein by kodein()

    override val kodein: Kodein by retainedKodein {

        extend(_parentKodein, copy = Copy.All)

        // bind<Activity>() with singleton { this@BaseActivity }

        bind<Context>(DI_ACTIVITY_CONTEXT) with singleton { this@BaseActivity }

        import(moduleDependency, true)
    }

    protected lateinit var layoutBinding: LayoutBinding

    lateinit var viewModel: VM

    private val viewModelFactory: ViewModelProvider.Factory by instance()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation =
            if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        bindContentView(layoutId())

        window.changeStatusBarColor(layoutBinding.root, R.color.colorTransparent)
        // In Activity's onCreate() for instance
        // In Activity's onCreate() for instance
        /* val w: Window = window
         w.setFlags(
             WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
             WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
         )*/

        initiate()
    }

    private fun bindContentView(layoutId: Int) {
        layoutBinding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(viewModelClass)
    }

    abstract val viewModelClass: Class<VM>

    @LayoutRes
    protected abstract fun layoutId(): Int

    abstract fun initiate()

    /*override fun onBackPressed() {
        super.onBackPressed()
        killVisibleActivity()
    }


    open fun killVisibleActivity() {
        BaseAppController.setExitPageAnimation(this)
        finish()
    }*/

}