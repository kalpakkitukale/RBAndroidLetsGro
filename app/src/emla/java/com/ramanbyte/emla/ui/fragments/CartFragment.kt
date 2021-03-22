package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCartBinding
import com.ramanbyte.emla.adapters.CartAdapter
import com.ramanbyte.emla.models.response.CartResponseModel
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
                        try {
                            val filterList= it.sortedBy {
                                it.courseFee
                            }

                            val cartAdapter = CartAdapter(viewModel, filterList, mContext)
                            rvCartFragment.apply {
                                layoutManager =
                                    LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                                adapter = cartAdapter
                            }
                            finalCartList = cartListLiveData.value as ArrayList<CartResponseModel>
                            cartListLiveData.value = null
                        }catch (e:Exception){
                            e.printStackTrace()
                            AppLog.infoLog(e.message.toString())
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
}
