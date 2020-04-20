package com.ramanbyte.emla.ui.fragments

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentChaptersSectionListBinding
import com.ramanbyte.emla.adapters.ChaptersSectionListAdapter
import com.ramanbyte.emla.content.ContentViewer
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.view_model.ChaptersSectionViewModel
import com.ramanbyte.utilities.*

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

        arguments?.apply {
            viewModel.coursesModel = getParcelable(KEY_COURSE_MODEL)
            viewModel.chaptersModel = getParcelable(KEY_CHAPTER_MODEL)

            setToolbarTitle(viewModel.chaptersModel?.chapterName!!)
        }

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

            getList(chaptersModel?.chapterId ?: 0)

            getList()?.observe(this@ChaptersSectionListFragment, Observer {

                chaptersSectionListAdapter?.submitList(it)

            })

            contentMutableList?.observe(this@ChaptersSectionListFragment, Observer {

                it?.apply {

                    if (isNotEmpty()) {

                        forEach { contentModel ->

                            ContentViewer(context!!, viewModel).download(
                                contentModel,
                                MediaInfoModel().apply {
                                    chapterId = viewModel.chaptersModel?.chapterId ?: 0
                                    courseId = viewModel.coursesModel?.courseId ?: 0
                                    courseName = viewModel.coursesModel?.courseName ?: ""
                                    chapterName =
                                        viewModel.chaptersModel?.chapterName ?: ""
                                })
                        }
                    }
                }
            })

            errorMessage.observe(this@ChaptersSectionListFragment, Observer {
                if (it != null) {
                    layoutBinding.containerMain.snackbar(it)
                }
            })
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
