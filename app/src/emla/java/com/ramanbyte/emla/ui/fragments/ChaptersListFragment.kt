package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentChaptersListBinding
import com.ramanbyte.emla.adapters.ChaptersListAdapter
import com.ramanbyte.emla.view_model.ChaptersViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * A simple [Fragment] subclass.
 */
class ChaptersListFragment : BaseFragment<FragmentChaptersListBinding, ChaptersViewModel>() {

    override val viewModelClass: Class<ChaptersViewModel> = ChaptersViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_chapters_list

    private var chaptersListAdapter: ChaptersListAdapter? = null

    override fun initiate() {

        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        layoutBinding?.apply {

            lifecycleOwner = this@ChaptersListFragment

            chaptersViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

        }

        setAdapter()
        viewModelOps()
    }

    private fun setAdapter() {

        layoutBinding?.apply {

            rvChapterList.apply {

                chaptersListAdapter = ChaptersListAdapter(viewModel)

                adapter = chaptersListAdapter

            }
        }
    }

    private fun viewModelOps() {

        viewModel.apply {

            getList(0)

            getList()?.observe(this@ChaptersListFragment, Observer {

                chaptersListAdapter?.submitList(it)

            })

        }

    }
}
