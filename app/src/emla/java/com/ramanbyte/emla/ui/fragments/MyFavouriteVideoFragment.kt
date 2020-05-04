package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentMyFavouriteVideoBinding
import com.ramanbyte.emla.adapters.MyFavouriteVideosListAdapter
import com.ramanbyte.emla.view_model.MyFavouriteVideoViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.ProgressLoader
import kotlinx.android.synthetic.emla.exo_playback_control_view.*

/**
 * A simple [Fragment] subclass.
 */
class MyFavouriteVideoFragment : BaseFragment<FragmentMyFavouriteVideoBinding,MyFavouriteVideoViewModel>() {

    var mContext : Context? = null

    override val viewModelClass: Class<MyFavouriteVideoViewModel> = MyFavouriteVideoViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_my_favourite_video

    override fun initiate() {
        layoutBinding.apply {

            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            lifecycleOwner = this@MyFavouriteVideoFragment
            myFavouriteVideoViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

            viewModel.apply {

                getFavouriteVideos()

                favouriteVideosListLiveData.observe(this@MyFavouriteVideoFragment, Observer {
                    if (it != null) {
                        val downloadsListAdapter =
                            MyFavouriteVideosListAdapter(viewModel, it)

                        rvMyFavourite?.apply {

                            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

                            adapter = downloadsListAdapter
                        }
                    }
                })

                onClickFavouriteVideosLiveData.observe(this@MyFavouriteVideoFragment, Observer {
                    if (it != null){
                        if (it == true){
                            AppLog.infoLog("onClickFavouriteVideosLiveData")
                            onClickFavouriteVideosLiveData.value = false
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
