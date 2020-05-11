package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardMyFavouriteVideosBinding
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.view_model.MyFavouriteVideoViewModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.NetworkConnectivity
import kotlinx.android.synthetic.emla.exo_playback_control_view.*

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

                /*btnFavouriteVideo.setOnClickListener(View.OnClickListener {

                    favouriteViewModel.apply {
                        if (NetworkConnectivity.isConnectedToInternet()) {
                            setAlertDialogResourceModelMutableLiveData(
                                BindingUtils.string(R.string.message_remove_favourite),
                                null,
                                false,
                                positiveButtonText = BindingUtils.string(R.string.strYes),
                                positiveButtonClickFunctionality = {
                                    btnFavouriteVideo.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart_with_black_border))
                                    favouriteViewModel.onClickFavouriteVideosLiveData.value = favouriteVideosModel.contentId
                                    isAlertDialogShown.value = false
                                },
                                negativeButtonText = BindingUtils.string(R.string.strNo),
                                negativeButtonClickFunctionality = {
                                    isAlertDialogShown.value = false
                                })

                            isAlertDialogShown.value = true
                        }else{
                            showNoInternetDialog()
                        }
                    }

                })*/
            }
        }

    }
}


