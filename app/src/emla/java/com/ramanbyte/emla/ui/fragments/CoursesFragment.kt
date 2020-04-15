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
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.*

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */
class CoursesFragment : BaseFragment<FragmentCoursesBinding, CoursesViewModel>() {
    private lateinit var mContext: Context
    private var coursesAdapter: CoursesAdapter? = null

    override val viewModelClass: Class<CoursesViewModel>
        get() = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_courses

    override fun initiate() {
        layoutBinding.apply {
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
                    AppLog.infoLog("selectedCourseModelLiveData")
                    it?.apply {

                        activity?.apply {
                            if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {

                                if (preAssessmentStatus.equals("true", true)) {
                                    AppLog.infoLog("courses details page")
                                    /*CourseDetailActivity.intent(this).apply {
                                        putExtra(KEY_COURSE_MODEL, it)
                                    }*/
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

                    }

                })
        }
    }

    var menu: Menu? = null
    private var mSearchView: SearchView? = null
    var searchItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_course_search, menu)

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
                viewModel.searchQuery.postValue(query.toString())
                mSearchView!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.toString() == KEY_BLANK) {
                    // viewModel.searchQuery.postValue(newText.toString())
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_key -> {
                mSearchView!!.setQuery(
                    KEY_BLANK, false
                )
                mSearchView!!.clearFocus()
                mSearchView!!.isIconified = true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
