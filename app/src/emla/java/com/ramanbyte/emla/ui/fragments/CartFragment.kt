package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCartBinding
import com.ramanbyte.emla.adapters.CartAdapter
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.CartViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.ProgressLoader
import java.util.logging.Handler

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
                clickedLiveData.postValue(false)
                cartListLiveData.observe(this@CartFragment, Observer {
                    if (it != null) {
                        val cartAdapter = CartAdapter(viewModel, it, mContext)
                        rvCartFragment.apply {
                            layoutManager =
                                LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                            adapter = cartAdapter
                        }
                        finalCartList = cartListLiveData.value as ArrayList<CartResponseModel>
                        cartListLiveData.value = null
                    }
                })
// free course added sucessfully then it redirect to my course fragment
                freeCourseAddSucessfullyLiveData.observe(this@CartFragment, Observer {
                    it?.let {
                        if (it != null && it != 0 && paidCourse.size == 0) {
                            setAlertDialogResourceModelMutableLiveData(
                                BindingUtils.string(R.string.unpaid_transaction_sucessful),
                                BindingUtils.drawable(R.drawable.ic_all_the_best)!!,
                                true,
                                BindingUtils.string(R.string.strOk), {
                                    viewModel.gotoMyCourse(this@CartFragment.requireView())
                                    /*findNavController().navigate(R.id.action_cartFragment_to_myCourseFragment)*/
                                    isAlertDialogShown.postValue(false)
                                    clickedLiveData.postValue(false)
                                    freeCourseAddSucessfullyLiveData.postValue(0)
                                })
                            isAlertDialogShown.postValue(true)
                        }

                    }
                })

                clickedLiveData.observe(this@CartFragment, Observer {
                    it?.let {
                        if (it){
                            layoutBinding.btnProceedPay.isClickable = false
                        }else{
                            layoutBinding.btnProceedPay.isClickable = true
                        }
                    }
                })
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        viewModel.clickedLiveData.postValue(false)
        super.onResume()
    }
}
