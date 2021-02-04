package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardCartItemBinding
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.CartViewModel

class CartAdapter:  RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    var mContext : Context? = null
    var cartViewModels: CartViewModel? = null
    var cartList: ArrayList<CartResponseModel>? =
        null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.CartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CartViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.card_cart_item,
                parent,
                false
            )
        )
    }

    fun setData(sessionList: ArrayList<CartResponseModel>){
        cartList = sessionList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return cartList?.size ?: 0
    }

    override fun onBindViewHolder(
        holder: CartAdapter.CartViewHolder,
        position: Int
    ) {
        holder.bind(cartList?.get(position))
    }


    inner class CartViewHolder(private var layoutBinding: CardCartItemBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {
        fun bind(cartResponseModel: CartResponseModel?) {
            layoutBinding?.apply {
                cartModel = cartResponseModel
                cartViewModel = cartViewModels
            }
        }
    }
}
