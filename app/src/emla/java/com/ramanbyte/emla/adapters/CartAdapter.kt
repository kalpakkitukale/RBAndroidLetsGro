package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardCartItemBinding
import com.ramanbyte.databinding.CardMyFavouriteVideosBinding
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.CartViewModel
import com.ramanbyte.emla.view_model.MyFavouriteVideoViewModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.NetworkConnectivity
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx
import kotlinx.android.synthetic.emla.exo_playback_control_view.*

class CartAdapter(
    private var cartViewModel: CartViewModel,
    private var cartList: List<CartResponseModel>,
    private var context: Context
) :
    RecyclerView.Adapter<CartAdapter.MyCartViewHolder>() {

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


