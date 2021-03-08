package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardTransactionHistoryBinding
import com.ramanbyte.emla.models.TransactionHistoryModel
import com.ramanbyte.emla.view_model.TransactionHistoryViewModel

/**
 * Created by Mansi Manakiki on 11 Feb, 2021
 * Email : mansi.m@ramanbyte.com
 */

class TransactionHistoryListAdapter(
    private var transactionHistoryViewModel: TransactionHistoryViewModel,
    private var transactionHistoryList: List<TransactionHistoryModel>
) :
    RecyclerView.Adapter<TransactionHistoryListAdapter.TransactionHistoryViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionHistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return TransactionHistoryViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.card_transaction_history,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return transactionHistoryList.size
    }

    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        holder.bind(transactionHistoryList[position])
    }


    inner class TransactionHistoryViewHolder(private var cardBinding: CardTransactionHistoryBinding) :
        RecyclerView.ViewHolder(cardBinding.root) {

        fun bind(transactionModel: TransactionHistoryModel) {
            cardBinding.apply {
                viewModel = transactionHistoryViewModel
                this.transactionHistoryModel = transactionModel
            }
        }

    }
}

