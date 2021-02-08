package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.utilities.S3Constant.Companion.mContext
import com.ramanbyte.databinding.CardCartItemBinding
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.ui.fragments.CartFragment
import com.ramanbyte.emla.view_model.CartViewModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

class CartAdapter(
    private var cartViewModel: CartViewModel,
    private var cartList: List<CartResponseModel>,
    private var context: Context
) :
    RecyclerView.Adapter<CartAdapter.MyCartViewHolder>() {

    var course: Float = 0.0f
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return MyCartViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.card_cart_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {

        val cartModel: CartResponseModel = cartList[position]
        holder.bind(cartModel.apply {
            courseImageUrl =
                StaticMethodUtilitiesKtx.getS3DynamicURL(courseImage ?: KEY_BLANK, context!!)
        })
    }



    inner class MyCartViewHolder(private var cardCartItemBinding: CardCartItemBinding) :
        RecyclerView.ViewHolder(cardCartItemBinding.root) {

        fun bind(responseModel: CartResponseModel) {
            cardCartItemBinding.apply {
                viewModel = cartViewModel
                this.cartModel = responseModel
            }
        }

    }
}


