package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentMyDownloadsBinding
import com.ramanbyte.emla.adapters.MyDownloadsListAdapter
import com.ramanbyte.emla.content.ContentViewer
import com.ramanbyte.emla.content.ExoMediaDownloadUtil
import com.ramanbyte.emla.view_model.MyDownloadsViewModel
import com.ramanbyte.utilities.AlertDialog

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 *
 */
class MyDownloadsFragment : BaseFragment<FragmentMyDownloadsBinding, MyDownloadsViewModel>() {

    override val viewModelClass: Class<MyDownloadsViewModel> = MyDownloadsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_my_downloads

    override fun initiate() {

        AlertDialog(requireContext(), viewModel)

        layoutBinding.apply {

            lifecycleOwner = this@MyDownloadsFragment
            myDownloadsViewModel = viewModel
            noData.viewModel = viewModel

        }

        ExoMediaDownloadUtil.loadDownloads(requireContext())

        setMediaRecycler()

        setViewModelOps()

    }

    private fun setMediaRecycler() {

        val mediaList = viewModel.getMedias()

        layoutBinding?.apply {

            if (mediaList != null) {

                val downloadsListAdapter =
                    MyDownloadsListAdapter(downloadsViewModel = viewModel, mediaList = mediaList)

                listDownloads?.apply {
                    layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    adapter = downloadsListAdapter
                }
            }
        }

    }

    private fun setViewModelOps() {

        viewModel.apply {

            playOrPreviewLiveData.observe(viewLifecycleOwner, Observer { mediaInfoModel ->

                if (mediaInfoModel != null) {
                    ContentViewer(requireContext()).preview(mediaInfoModel)
                    playOrPreviewLiveData.value = null
                }
            })

            deleteMediaLiveData.observe(viewLifecycleOwner, Observer { mediaInfoModel ->

                if (mediaInfoModel != null) {

                    if (ContentViewer(requireContext()).deleteMediaOrFiles(mediaInfoModel)) {
                        viewModel.deleteMediaInfo(mediaInfoModel.mediaId)
                        viewModel.showDeleteSuccessDialog()
                        setMediaRecycler()
                    } else {
                        viewModel.showDeleteErrorDialog()
                    }

                    deleteMediaLiveData.value = null
                }
            })

        }
    }
}
