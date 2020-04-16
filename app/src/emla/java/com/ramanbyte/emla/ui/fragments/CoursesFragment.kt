package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCoursesBinding
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
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
                coursesAdapter = CoursesAdapter((activity!!).displayMetrics())
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = coursesAdapter?.apply {
                    this.context = mContext
                    viewModel = this@CoursesFragment.viewModel
                }
            }
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {
            initPaginationResponseHandler()

            coursesPagedList()?.observe(this@CoursesFragment, androidx.lifecycle.Observer {
                it?.let { coursesAdapter?.apply { submitList(it) } }
            })

            selectedCourseModelLiveData.observe(
                this@CoursesFragment, Observer {
                    it?.apply {

                        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {

                            if (preAssessmentStatus.equals("true", true)) {
                                val bundle = Bundle()
                                bundle.putParcelable(KEY_COURSE_MODEL, it)
                                view?.findNavController()
                                    ?.navigate(R.id.courseDetailFragment, bundle)
                            } else {
                                val bundle = Bundle()
                                bundle.putParcelable(KEY_COURSE_MODEL, it)
                                bundle.putInt(keyTestType, KEY_QUIZ_TYPE_ASSESSMENT)
                                view?.findNavController()
                                    ?.navigate(R.id.preAssessmentTestFragment, bundle)
                            }

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


                    }

                })

            isFilterApplied.observe(this@CoursesFragment, Observer {
                it?.let {
                    setupBadge(it)
                    isFilterApplied.postValue(null)
                }
            })
        }
    }

    var menu: Menu? = null
    private var mSearchView: SearchView? = null
    var searchItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_course_search, menu)

        val menuItem = menu.findItem(R.id.action_filter)

        val actionView = menuItem.actionView
        badgeView = actionView.findViewById(R.id.filter_badge) as View
        setupBadge()
        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        searchItem = menu.findItem(R.id.action_search_key)

        mSearchView = searchItem?.actionView as SearchView

        val searchEditText = mSearchView!!.findViewById(R.id.search_src_text) as EditText

        searchEditText.setTextColor(BindingUtils.color(R.color.colorWhite))
        searchEditText.setHintTextColor(BindingUtils.color(R.color.colorTransparent))
        searchEditText.hint = BindingUtils.string(R.string.search_by_course)

        val searchClose = mSearchView?.findViewById(R.id.search_close_btn) as ImageView
        searchClose.setImageResource(R.drawable.ic_close_white)
        val searchImgId = R.id.search_button
        val searchIcon = mSearchView?.findViewById(searchImgId) as ImageView
        searchIcon.setImageResource(R.drawable.ic_search)

        mSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // api call
//                viewModel.searchQuery.postValue(query.toString())
//                mSearchView!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery.postValue(newText.toString())
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
                if (badgeView?.visibility != View.VISIBLE) {
                    badgeView?.visibility = View.VISIBLE
                }
            } else {
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
