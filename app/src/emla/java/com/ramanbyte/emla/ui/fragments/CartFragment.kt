package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCartBinding
import com.ramanbyte.emla.adapters.CartAdapter
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.adapters.MyFavouriteVideosListAdapter
import com.ramanbyte.emla.content.ContentViewer
import com.ramanbyte.emla.ui.activities.PaymentSummaryActivity
import com.ramanbyte.emla.view.RecommendedCourseFilterBottomSheet
import com.ramanbyte.emla.view_model.CartViewModel
import com.ramanbyte.utilities.*

class CartFragment : BaseFragment<FragmentCartBinding, CartViewModel>() {
    private lateinit var mContext: Context
    override val viewModelClass: Class<CartViewModel>
        get() = CartViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_cart

    override fun initiate() {
        layoutBinding.apply {

            ProgressLoader(mContext, viewModel)
            AlertDialog(mContext, viewModel)

            lifecycleOwner = this@CartFragment
            cartViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

            viewModel.apply {
                getCartList()
                cartListLiveData.observe(this@CartFragment, Observer {
                    if (it != null) {

                        val cartAdapter = CartAdapter(viewModel, it, mContext)
                        rvCartFragment.apply {
                            layoutManager =
                                LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                            adapter = cartAdapter

                        }
                        cartListLiveData.value = null
                    }
                })
            }
        }
    }


    /*override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@CartFragment
            ProgressLoader(mContext, viewModel)
            AlertDialog(mContext, viewModel)
            cartViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
            cartFragment = this@CartFragment
        }

        setAdapter()
        setViewModelOp()
    }
    private fun setAdapter() {
        layoutBinding.rvCartFragment.apply {
            cartAdapter?.apply {
                layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL,
                        false
                    )
                this.cartList = viewModel.responseList
                this.cartViewModels = viewModel
                adapter = this
            }
        }
    }
    //share Course Details through a link
    private fun setViewModelOp() {
        viewModel.apply {

            getCartList()
            cartListLiveData?.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    it?.let {
                        cartAdapter?.apply {
                            setData(it)
                        }
                        cartListLiveData.value = null
                    }
                })
        }
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartList()
    }

    fun clickOnPay(view: View) {
        startActivityForResult(
            PaymentSummaryActivity.openPaymentActivity(
                requireContext(),
                0,
                0,
                "campusName",
                0,
                "programName",
                "1",
                10.toString(),
                ""
            ),
            PAYMENT_SUCCESSFUL_REQUEST_CODE
        )
    }

}
