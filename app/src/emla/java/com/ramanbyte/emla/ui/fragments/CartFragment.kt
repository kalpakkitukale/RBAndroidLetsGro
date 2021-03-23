package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
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
                        finalCartList = cartListLiveData.value as ArrayList<CartResponseModel>
                        cartListLiveData.value = null
                    }
                })
// free course added sucessfully then it redirect to my course fragment
                freeCourseAddSucessfullyLiveData.observe(this@CartFragment, Observer {
                    it?.let {
                        if (it != null && it != 0 && paidCourse.size == 0) {
                            setAlertDialogResourceModelMutableLiveData(
                                BindingUtils.string(R.string.transaction_sucessful),
                                BindingUtils.drawable(R.drawable.ic_all_the_best)!!,
                                true,
                                BindingUtils.string(R.string.strOk), {
                                    view?.findNavController()?.navigate(R.id.myCourseFragment)
                                    isAlertDialogShown.postValue(false)
                                })
                            isAlertDialogShown.postValue(true)
                        }
                        freeCourseAddSucessfullyLiveData.postValue(0)
                    }
                }
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
