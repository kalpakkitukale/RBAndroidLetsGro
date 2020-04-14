package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentChaptersSectionListBinding
import com.ramanbyte.emla.adapters.ChaptersSectionListAdapter
import com.ramanbyte.emla.view_model.ChaptersSectionViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * A simple [Fragment] subclass.
 */
class ChaptersSectionListFragment :
    BaseFragment<FragmentChaptersSectionListBinding, ChaptersSectionViewModel>() {

    override val viewModelClass: Class<ChaptersSectionViewModel> =
        ChaptersSectionViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_chapters_section_list

    private var chaptersSectionListAdapter: ChaptersSectionListAdapter? = null

    override fun initiate() {

        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        layoutBinding?.apply {

            lifecycleOwner = this@ChaptersSectionListFragment

            chaptersSectionViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

        }

        setAdapter()
        viewModelOps()
    }

    private fun setAdapter() {

        layoutBinding?.apply {

            rvSectionList.apply {

                chaptersSectionListAdapter = ChaptersSectionListAdapter(viewModel)

                adapter = chaptersSectionListAdapter

            }
        }
    }

    private fun viewModelOps() {

        viewModel.apply {

            getList(0)

            getList()?.observe(this@ChaptersSectionListFragment, Observer {

                chaptersSectionListAdapter?.submitList(it)

            })

        }

    }
}
