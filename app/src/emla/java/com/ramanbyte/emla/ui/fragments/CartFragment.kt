package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCartBinding
import com.ramanbyte.emla.adapters.CartAdapter
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.view.RecommendedCourseFilterBottomSheet
import com.ramanbyte.emla.view_model.CartViewModel
import com.ramanbyte.utilities.*

class CartFragment : BaseFragment<FragmentCartBinding, CartViewModel>() {
    private lateinit var mContext: Context
    private var cartAdapter: CartAdapter? = null

    override val viewModelClass: Class<CartViewModel>
        get() = CartViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_cart

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@CartFragment
            ProgressLoader(mContext, viewModel)
            AlertDialog(mContext, viewModel)
            cartViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        setAdapter()
        setViewModelOp()
    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvCartFragment.apply {
              //  coursesAdapter = CartAdapter((activity!!).displayMetrics())
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = cartAdapter?.apply {
                    this.mContext = mContext
                    viewModel = this@CartFragment.viewModel
                }
            }
        }
    }

    //share Course Details through a link
    private fun setViewModelOp() {
        viewModel.apply {

            initPaginationResponseHandler()
           coursesPagedList()?.observe(this@CartFragment, androidx.lifecycle.Observer {
        //    it?.let { coursesAdapter?.apply { submitList(it) } }
    })


}
}





override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
