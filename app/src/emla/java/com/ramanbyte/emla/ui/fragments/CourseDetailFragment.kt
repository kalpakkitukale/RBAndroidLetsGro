package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCourseDetailBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.models.ChapterModel
import com.ramanbyte.emla.models.CoursesModel
import androidx.lifecycle.Observer
import com.ramanbyte.emla.models.CourseSyllabusModel

import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.fragment_course_detail.*

/**
 * A simple [Fragment] subclass.
 */
class CourseDetailFragment : BaseFragment<FragmentCourseDetailBinding, CoursesViewModel>() {


    private var courseModel: CoursesModel? = null
    private var chapterModel: ChapterModel? = null
    private var menu: Menu? = null

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override val viewModelClass: Class<CoursesViewModel> = CoursesViewModel::class.java
    override fun layoutId(): Int = R.layout.fragment_course_detail

    override fun initiate() {

        if (intent != null) {
            courseModel = intent.getParcelableExtra(KEY_COURSE_MODEL)!!

            courseModel?.courseImageUrl =
                AppS3Client.createInstance(this).getFileAccessUrl("dev/"+courseModel?.courseImage?: KEY_BLANK) ?: ""
        }

        viewModel.coursesModelLiveData.value = courseModel
        //setUpViewPager(it)

        viewModel.apply {

            getCoursesSyllabus()

            courseSyllabusModel.observe(this@CourseDetailFragment, Observer {
                if (it != null) {
                    menu?.findItem(R.id.view_certificate)?.isVisible =
                        it.summativeAssessmentStatus.equals(
                            "true",
                            true
                        )
                } else {
                    menu?.findItem(R.id.view_certificate)?.isVisible = false
                }
            })

            viewModel.courseSyllabusModel.observe(this@CourseDetailFragment, Observer {
                if (it != null)
                    setUpViewPager(it)

            })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_certificate, menu)
        this.menu = menu
        return true

    }


    private fun setUpViewPager(it: CourseSyllabusModel) {
        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.POSITION_UNCHANGED
        )

        viewPagerAdapter?.addFragmentView(CourseSyllabusFragment.getInstance(), "")
        viewPagerAdapter?.addFragmentView(CourseSessionFragment.getInstance(), "")
        if (!it.summativeAssessmentStatus.isNullOrEmpty())
            viewPagerAdapter?.addFragmentView(CourseResultFragment.getInstance(), "")

        viewPagerCourse.adapter = viewPagerAdapter
        tabLayoutCourse.setupWithViewPager(viewPagerCourse)

        setupTabIcon()

    }

    private fun setupTabIcon() {
        tabLayoutCourse.getTabAt(0)?.icon = BindingUtils.drawable(R.drawable.ic_open_book)
        tabLayoutCourse.getTabAt(1)?.icon = BindingUtils.drawable(R.drawable.ic_education)
        tabLayoutCourse.getTabAt(2)?.icon = BindingUtils.drawable(R.drawable.ic_timeline)
    }

    override fun onResume() {
        super.onResume()
        //layoutBinding.appBar.title = BindingUtils.string(R.string.basics_of_sales)
    }

}

