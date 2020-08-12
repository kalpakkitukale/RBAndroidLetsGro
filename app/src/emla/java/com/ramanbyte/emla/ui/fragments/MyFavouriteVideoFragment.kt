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
import com.ramanbyte.emla.content.ContentViewer
import com.ramanbyte.emla.view_model.MyFavouriteVideoViewModel
import com.ramanbyte.utilities.*
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
                        val favouriteVideosListAdapter =
                            MyFavouriteVideosListAdapter(viewModel, it)

                        rvMyFavourite?.apply {

                            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

                            adapter = favouriteVideosListAdapter
                        }
                    }
                })

                playOrPreviewLiveData.observe(viewLifecycleOwner, Observer { mediaInfoModel ->

                    if (mediaInfoModel != null) {

                        ContentViewer(activity!!).preview(mediaInfoModel)

                        playOrPreviewLiveData.value = null

                    }
                })

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavouriteVideos()
    }

}
