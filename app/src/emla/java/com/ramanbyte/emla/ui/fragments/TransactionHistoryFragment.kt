package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentTransactionHistoryBinding
import com.ramanbyte.emla.adapters.TransactionHistoryListAdapter
import com.ramanbyte.emla.view.CoursePerchesDetailsBottomSheet
import com.ramanbyte.emla.view_model.TransactionHistoryViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * Created by Mansi Manakiki on 11 Feb, 2021
 * Email : mansi.m@ramanbyte.com
 */

class TransactionHistoryFragment :
    BaseFragment<FragmentTransactionHistoryBinding, TransactionHistoryViewModel>() {
    var mContext: Context? = null

    var courseDetailsBottomSheet: CoursePerchesDetailsBottomSheet ?= null
    override val viewModelClass: Class<TransactionHistoryViewModel>
        get() = TransactionHistoryViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_transaction_history

    override fun initiate() {
     layoutBinding.apply {

            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            lifecycleOwner = this@TransactionHistoryFragment
            transactionHistoryViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

           viewModel.apply {

                getTransactionsHistory()

                 transactionHistoryListLiveData.observe(this@TransactionHistoryFragment, Observer {
                    if (it != null) {
                         val adpter = TransactionHistoryListAdapter(viewModel, it)
                         rvMyTransactions.apply {
                             layoutManager =
                                 LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                             adapter = adpter
                         }
                     }
                    //transactionHistoryListLiveData.value = null
                 })


               coureseDetailClicked.observe(this@TransactionHistoryFragment, Observer {
                   it?.let {
                       if (it)
                       courseDetailsBottomSheet = CoursePerchesDetailsBottomSheet(false,true)
                       courseDetailsBottomSheet?.show(childFragmentManager,"Courece Details")

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