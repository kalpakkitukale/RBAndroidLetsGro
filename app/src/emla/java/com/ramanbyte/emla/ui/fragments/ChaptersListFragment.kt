package com.ramanbyte.emla.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseParentFragment
import com.ramanbyte.databinding.FragmentChaptersListBinding
import com.ramanbyte.emla.adapters.ChaptersListAdapter
import com.ramanbyte.emla.content.ContentViewer
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.view_model.ChaptersViewModel
import com.ramanbyte.emla.view_model.CoursesDetailViewModel
import com.ramanbyte.utilities.*

/**
 * A simple [Fragment] subclass.
 */
class ChaptersListFragment :
    BaseParentFragment<FragmentChaptersListBinding, ChaptersViewModel, CoursesDetailViewModel>() {

    override val viewModelClass: Class<ChaptersViewModel> = ChaptersViewModel::class.java

    override val parentViewModelClass: Class<CoursesDetailViewModel> =
        CoursesDetailViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_chapters_list

    private var chaptersListAdapter: ChaptersListAdapter? = null

    override fun initiateView() {

        viewModel.courseModel = parentViewModel.coursesModelLiveData.value
        viewModel.courseSyllabusModel = parentViewModel.courseSyllabusModelLiveData.value

        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        val width =
            (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)
        val layoutParams = layoutBinding.imgViewCourseSession.layoutParams
        layoutParams.height = (width * 0.6).toInt()
        layoutBinding.imgViewCourseSession.layoutParams = layoutParams
        layoutBinding.imgViewCourseSession.layoutParams.apply {
            height = (width * 0.6).toInt()
        }

        layoutBinding?.apply {

            lifecycleOwner = this@ChaptersListFragment
            courseDetailViewModel = parentViewModel
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

            getList(parentViewModel.coursesModelLiveData?.value?.courseId ?: 0)

            getList()?.observe(this@ChaptersListFragment, Observer {

                chaptersListAdapter?.submitList(it)

            })

            selectedChaptersModelLiveData.observe(
                this@ChaptersListFragment,
                Observer { chaptersModel ->

                    chaptersModel?.apply {
                        parentViewModel.selectedChaptersModelLiveData.value = this
                        selectedChaptersModelLiveData.value = null
                    }

                })

            isAllCourseSessionCompleted.observe(this@ChaptersListFragment, Observer {
                if (it == true) {
                    layoutBinding.btnAppearSummativeTest.visibility = View.VISIBLE
                } else {
                    layoutBinding.btnAppearSummativeTest.visibility = View.GONE
                }
            })

            showValidationMessage.observe(this@ChaptersListFragment, Observer {
                if (it != null) {
                    layoutBinding.containerLayout.snackbar(it)
                    showValidationMessage.value = null
                }
            })

            contentMutableList?.observe(this@ChaptersListFragment, Observer {

                it?.apply {

                    if (isNotEmpty()) {

                        forEach { contentModel ->

                            ContentViewer(context!!, viewModel).download(
                                contentModel,
                                MediaInfoModel().apply {
                                    chapterId = viewModel.downloadRequestedChapter?.chapterId ?: 0
                                    courseId = viewModel.courseModel?.courseId ?: 0
                                    courseName = viewModel.courseModel?.courseName ?: ""
                                    chapterName =
                                        viewModel.downloadRequestedChapter?.chapterName ?: ""
                                })
                        }
                    }
                }
            })

        }

    }
}
