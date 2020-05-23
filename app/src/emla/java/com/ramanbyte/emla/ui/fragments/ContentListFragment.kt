package com.ramanbyte.emla.ui.fragments

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentContentListBinding
import com.ramanbyte.emla.content.ContentViewer
import com.ramanbyte.emla.data_layer.repositories.ContentListAdapter
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.view_model.ContentViewModel
import com.ramanbyte.utilities.*

/**
 * A simple [Fragment] subclass.
 */
class ContentListFragment : BaseFragment<FragmentContentListBinding, ContentViewModel>() {

    override val viewModelClass: Class<ContentViewModel> = ContentViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_content_list

    override fun initiate() {

        ProgressLoader(context!!, viewModel!!)
        AlertDialog(context!!, viewModel!!)

        layoutBinding.apply {

            lifecycleOwner = this@ContentListFragment

            contentViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        viewModelOps()
    }

    private fun setAdapter(contentList: ArrayList<ContentModel>) {

        layoutBinding.apply {
            rvContent.adapter = ContentListAdapter(viewModel, contentList)
        }
    }

    private fun viewModelOps() {

        viewModel.apply {

            arguments?.apply {

                sectionId = getInt(keySectionId, 0)!!
                sectionName = getString(keySectionName)

                courseId = getInt(keyCourseId, 0)
                chapterId = getInt(keyChapterId, 0)
                courseName = getString(keyCourseName)
                chapterName = getString(keyChapterName)

                setToolbarTitle(sectionName!!)
            }

            getContentList()

            contentListLiveData.observe(this@ContentListFragment, Observer {

                it?.apply {
                    setAdapter(this)
                }

            })

            playOrPreviewLiveData.observe(
                this@ContentListFragment,
                Observer { contentModel ->

                    if (contentModel != null) {

                        ContentViewer(context!!, viewModel).preview(
                            contentModel,
                            MediaInfoModel().apply {
                                chapterId = viewModel.chapterId
                                courseId = viewModel.courseId ?: 0
                                courseName = viewModel.courseName ?: ""
                                chapterName = viewModel.chapterName ?: ""
                                sectionName = viewModel.sectionName!!
                                sectionId = viewModel.sectionId
                                likeVideo = contentModel.isLike ?: ""
                                favouriteVideo = contentModel.isFavourite ?: ""
                                contentTitle = contentModel.contentTitle ?: ""
                            })

                        playOrPreviewLiveData.value = null
                    }

                })

            downloadLiveData.observe(
                this@ContentListFragment,
                Observer { contentModel ->

                    if (contentModel != null) {

                        AppLog.infoLog("Download Clicked")
                        //     this@CourseTopicContentFragment.downloadContent(contentModel)

                        ContentViewer(context!!, viewModel).download(
                            contentModel,
                            MediaInfoModel().apply {
                                chapterId = viewModel.chapterId
                                courseId = viewModel.courseId ?: 0
                                courseName = viewModel.courseName ?: ""
                                chapterName = viewModel.chapterName ?: ""
                                sectionName = viewModel.sectionName!!
                                sectionId = viewModel.sectionId
                                likeVideo = contentModel.isLike ?: ""
                                favouriteVideo = contentModel.isFavourite ?: ""
                                contentTitle = contentModel.contentTitle ?: ""
                            })

                        downloadLiveData.value = null
                    }

                })

            deleteMediaLiveData.observe(
                this@ContentListFragment,
                Observer { mediaInfoModel ->

                    if (mediaInfoModel != null) {
                        ContentViewer(activity!!).deleteMediaOrFiles(mediaInfoModel)
                        viewModel.deleteMedia(mediaInfoModel.mediaId)
                        deleteMediaLiveData.value = null
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

    override fun onStart() {
        super.onStart()
        viewModel.getContentList()
    }
}
