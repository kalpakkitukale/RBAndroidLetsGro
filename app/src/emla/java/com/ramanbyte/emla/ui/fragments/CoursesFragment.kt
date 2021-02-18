package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.BuildConfig
import com.ramanbyte.R
import com.ramanbyte.R.id.search_src_text
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCoursesBinding
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.view.RecommendedCourseFilterBottomSheet
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.*

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class CoursesFragment : BaseFragment<FragmentCoursesBinding, CoursesViewModel>() {
    private lateinit var mContext: Context
    private var coursesAdapter: CoursesAdapter? = null
    private var courseFilterBottomSheet: RecommendedCourseFilterBottomSheet? = null

    override val viewModelClass: Class<CoursesViewModel>
        get() = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_courses

    override fun initiate() {

        /*if (!viewModel.isUserActive()) {
            findNavController().navigate(R.id.action_coursesFragment_to_learnerProfileFragment)
            return
        }*/

        layoutBinding.apply {
            setToolbarTitle(BindingUtils.string(R.string.courses))
            lifecycleOwner = this@CoursesFragment
            ProgressLoader(mContext, viewModel)
            AlertDialog(mContext, viewModel)
            coursesViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

        }

        setAdapter()
        setViewModelOp()
    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvCoursesFragment.apply {
                coursesAdapter = CoursesAdapter((activity!!).displayMetrics(), 0)
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = coursesAdapter?.apply {
                    this.context = mContext
                    viewModel = this@CoursesFragment.viewModel
                    setHasStableIds(true)
                }
                setHasFixedSize(true)
            }
        }
    }

    //share Course Details through a link
    private fun setViewModelOp() {
        viewModel.apply {

            initPaginationResponseHandler()
            coursesPagedList()?.observe(this@CoursesFragment, androidx.lifecycle.Observer {
                it?.let { coursesAdapter?.apply { submitList(it) } }
            })


            isFilterApplied.observe(this@CoursesFragment, Observer {
                it?.let {
                    AppLog.infoLog("CoursesFragment isFilterApplied ${it}")
                    setupBadge(it)
                    isFilterApplied.postValue(null)
                }
            })
            shareLiveData.observe(this@CoursesFragment, Observer {
                it?.let {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    val courseName: String? = it.courseName?.replace(" ".toRegex(), "%20")
                    val courseDescription: String? =
                        it.courseDescription?.replace(" ".toRegex(), "%20")
                    val courseImage: String? = it.courseImage?.replace(" ".toRegex(), "%20")
                    val data: String? =
                        courseName + "," + courseDescription + "," + it.courseCode + "," + courseImage + "," + it.totalCount
                    val link: String =
                        "details?id=" + BuildConfig.APPLICATION_ID + "&http://www.letsgro.in/course&url=&referrer=" + it.courseId + "," + data + "#Intent;scheme=market;action=android.intent.action.VIEW;package=com.android.vending;end\""
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        it.courseName + " " + BindingUtils.string(R.string.share_course) + " " + link
                    )
                    shareIntent.type = "text/plain"
                    startActivity(Intent.createChooser(shareIntent, "send to"))
                    shareLiveData.value = null
                }

            })
        }
    }

    var menu: Menu? = null
    private var mSearchView: SearchView? = null
    var searchItem: MenuItem? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_course_search, menu)

        val menuItem = menu.findItem(R.id.action_filter)

        val actionView = menuItem.actionView
        badgeView = actionView.findViewById(R.id.filter_badge) as View
        AppLog.infoLog("CoursesFragment onCreateOptionsMenu ${false}")
        AppLog.infoLog("CoursesFragment value ${viewModel.isFilterApplied.value}}")
        //setupBadge()

        setupBadge(viewModel.getFilterState())


        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        searchItem = menu.findItem(R.id.action_search_key)

        mSearchView = searchItem?.actionView as SearchView

        val searchEditText = mSearchView!!.findViewById(search_src_text) as EditText

        searchEditText.setTextColor(BindingUtils.color(R.color.colorIconNavyBlueInLightNGrayInDark))
        searchEditText.hint = BindingUtils.string(R.string.search_by_course_or_keywords)
        searchEditText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.sp_12)
        )

        searchEditText.setTextAppearance(R.style.AppTheme_Font)
        searchEditText.setHintTextColor(BindingUtils.color(R.color.colorTextHint))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //searchEditText.setTextCursorDrawable(BindingUtils.color(R.color.colorTextNavyBlueInLightNWhiteInDark))
        }

        val searchClose = mSearchView?.findViewById(R.id.search_close_btn) as ImageView
        searchClose.apply {
            setImageResource(R.drawable.ic_close_black)
            setOnClickListener {
                if (TextUtils.isEmpty(searchEditText.text.toString())) {
                    if (mSearchView?.isIconified == false) {
                        mSearchView?.isIconified = true
                    }
                } else {
                    searchEditText.setText("")
                    viewModel.searchQuery.postValue("")
                    mSearchView!!.clearFocus()
                }
            }
        }
        val searchImgId = R.id.search_button
        val searchIcon = mSearchView?.findViewById(searchImgId) as ImageView
        searchIcon.setImageResource(R.drawable.ic_baseline_search)



        mSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // api call
                viewModel.searchQuery.postValue(query.toString())
                mSearchView!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                viewModel.searchQuery.postValue(newText.toString())
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_key -> {
                mSearchView!!.setQuery(
                    KEY_BLANK, false
                )
                mSearchView!!.clearFocus()
                mSearchView!!.isIconified = true
                true
            }
            R.id.action_filter -> {
                viewModel.apply {
                    tempFilterModel.apply {
                        programId = filterRequestModel.programId
                        specializationId = filterRequestModel.specializationId
                        patternId = filterRequestModel.patternId
                        skillId = filterRequestModel.skillId
                    }
                }
                courseFilterBottomSheet = RecommendedCourseFilterBottomSheet()
                courseFilterBottomSheet?.show(childFragmentManager, "course_filter")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    var badgeView: View? = null
    private fun setupBadge(showCount: Boolean? = false) {
        if (badgeView != null) {
            if (showCount == true) {
                AppLog.infoLog("CoursesFragment badgeView VISIBLE")
                if (badgeView?.visibility != View.VISIBLE) {
                    badgeView?.visibility = View.VISIBLE
                }
            } else {
                AppLog.infoLog("CoursesFragment badgeView GONE")
                if (badgeView?.visibility != View.GONE) {
                    badgeView?.visibility = View.GONE
                }
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
