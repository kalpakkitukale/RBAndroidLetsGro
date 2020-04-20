package com.ramanbyte.emla.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCourseDetailBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.models.CourseSyllabusModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.ui.activities.CertificateViewerActivity
import com.ramanbyte.emla.view_model.CoursesDetailViewModel
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.fragment_course_detail.*

/**
 * A simple [Fragment] subclass.
 */
class CourseDetailFragment : BaseFragment<FragmentCourseDetailBinding, CoursesDetailViewModel>() {


    private var courseModel: CoursesModel? = null
    private var menu: Menu? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null

    override val viewModelClass: Class<CoursesDetailViewModel> = CoursesDetailViewModel::class.java
    override fun layoutId(): Int = R.layout.fragment_course_detail

    override fun initiate() {
        ProgressLoader(context!!, viewModel)

        arguments?.apply {
            courseModel = getParcelable(KEY_COURSE_MODEL)!!
        }

        setToolbarTitle(courseModel?.courseName!!)

        courseModel?.courseImageUrl =
            AppS3Client.createInstance(context!!).getFileAccessUrl(
                courseModel?.courseImage ?: KEY_BLANK
            ) ?: ""

        layoutBinding.apply {

            lifecycleOwner = this@CourseDetailFragment

            coursesDetailViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        viewModelOps()
    }

    private fun viewModelOps() {

        viewModel.apply {

            coursesModelLiveData.value = courseModel

            getCoursesSyllabus()

            courseSyllabusModelLiveData.observe(this@CourseDetailFragment, Observer {

                AppLog.debugLog("courseSyllabusModelLiveData called ------- ")
                if (menu != null) {
                    if (it != null && !it.summativeAssessmentStatus.isNullOrEmpty()) {
                        menu?.findItem(R.id.view_certificate)?.isVisible =
                            it.summativeAssessmentStatus.equals("true", true)
                    } else {
                        menu?.findItem(R.id.view_certificate)?.isVisible = false
                    }
                }
            })

            viewModel.courseSyllabusModelLiveData.observe(this@CourseDetailFragment, Observer {
                if (it != null)
                    setUpViewPager(it)

            })

            selectedChaptersModelLiveData.observe(
                this@CourseDetailFragment,
                Observer { chaptersModel ->

                    chaptersModel?.apply {

                        selectedChaptersModelLiveData.value = null

                        findNavController()
                            .navigate(
                                R.id.action_courseDetailFragment_to_chaptersSectionListFragment,
                                Bundle().apply {
                                    putParcelable(KEY_COURSE_MODEL, courseModel)
                                    putParcelable(KEY_CHAPTER_MODEL, chaptersModel)
                                })
                    }
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_certificate, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.view_certificate -> {
                if (NetworkConnectionInterceptor(context!!)?.isInternetAvailable()) {

                    val intent = Intent(context!!, CertificateViewerActivity::class.java)
                    intent.putExtra("userId", viewModel.userData?.userId)
                    intent.putExtra("courseId", viewModel.coursesModelLiveData.value?.courseId)
                    startActivity(intent)
                } else {
                    viewModel.apply {
                        setAlertDialogResourceModelMutableLiveData(
                            BindingUtils.string(R.string.no_internet_message),
                            BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                            true,
                            BindingUtils.string(R.string.yes), {
                                isAlertDialogShown.postValue(false)

                            },
                            BindingUtils.string(R.string.no), {
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setUpViewPager(it: CourseSyllabusModel) {
        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.POSITION_UNCHANGED
        )

        viewPagerAdapter?.addFragmentView(CourseSyllabusFragment.getInstance(), "")
        viewPagerAdapter?.addFragmentView(ChaptersListFragment(), "")
        AppLog.debugLog("summativeAssessmentStatus ------------------ ${it.summativeAssessmentStatus}")
        if (!it.summativeAssessmentStatus.isNullOrEmpty())
            viewPagerAdapter?.addFragmentView(CourseResultFragment.getInstance(), "")

        viewPagerCourse.adapter = viewPagerAdapter
        tabLayoutCourse.apply {
            setupWithViewPager(viewPagerCourse)
            getTabAt(0)?.icon = BindingUtils.drawable(R.drawable.ic_open_book)
            getTabAt(1)?.icon = BindingUtils.drawable(R.drawable.ic_education)
            getTabAt(2)?.icon = BindingUtils.drawable(R.drawable.ic_timeline)
        }

    }
}

