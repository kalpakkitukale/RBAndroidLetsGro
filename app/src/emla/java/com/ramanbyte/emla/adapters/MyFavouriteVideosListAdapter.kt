package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardMyFavouriteVideosBinding
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.view_model.MyFavouriteVideoViewModel

class MyFavouriteVideosListAdapter(
    private var favouriteViewModel: MyFavouriteVideoViewModel,
    private var favouriteVideosList: List<FavouriteVideosModel>
) :
    RecyclerView.Adapter<MyFavouriteVideosListAdapter.FavouriteVideosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteVideosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return FavouriteVideosViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.card_my_favourite_videos,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return favouriteVideosList.size
    }

    override fun onBindViewHolder(holder: FavouriteVideosViewHolder, position: Int) {
        holder.bind(favouriteVideosList[position])
    }


    inner class FavouriteVideosViewHolder(private var cardMyFavouriteVideosBinding: CardMyFavouriteVideosBinding) :
        RecyclerView.ViewHolder(cardMyFavouriteVideosBinding.root) {

        fun bind(favouriteVideosModel: FavouriteVideosModel) {
            cardMyFavouriteVideosBinding.apply {
                viewModel = favouriteViewModel
                this.favouriteVideosModel = favouriteVideosModel
            }
        }

    }
}


