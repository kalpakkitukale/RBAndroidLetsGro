package com.ramanbyte.emla.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentLearnerProfileBinding
import com.ramanbyte.databinding.PledgeDialogBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.utilities.*
import org.joda.time.DateTime
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class LearnerProfileFragment :
    BaseFragment<FragmentLearnerProfileBinding, LearnerProfileViewModel>() {

    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_learner_profile

    var pledgeDialog: Dialog? = null

    override fun initiate() {

        setToolbarTitle(BindingUtils.string(R.string.learner_profile))

        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        layoutBinding.apply {

            lifecycleOwner = viewLifecycleOwner

            learnerProfileViewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
            noInternet.viewModel = viewModel
        }

        viewModelOps()
    }

    fun viewModelOps() {

        viewModel.apply {

            getStates()

            registrationModelLiveData?.observe(viewLifecycleOwner, Observer { registrationModel ->

                registrationModel?.apply {

                    if (state != 0)
                        getCities(state ?: 0)

                    if (city != 0)
                        isCityAvailable = true

                    /*profileImageUrl.value =
                        AppS3Client.createInstance(context!!)
                            .getFileAccessUrl(userImageFilename ?: "")*/
                    profileImageUrl.value =
                        StaticMethodUtilitiesKtx.getS3DynamicURL(
                            userImageFilename ?: KEY_BLANK,
                            context!!
                        )

                    setValidation()
                    setupViewPager()
                }
            })

            showDatePickerDailogLiveData?.observe(viewLifecycleOwner, Observer {

                if (it) {
                    showDOBPickerDialog()
                    showDatePickerDailogLiveData?.value = false
                }

            })

            goToNextPageLiveData.observe(viewLifecycleOwner, Observer {

                layoutBinding.viewPageLearnerProfile.currentItem = 1

            })
            goToNexttPageLiveData.observe(viewLifecycleOwner,Observer{
                layoutBinding.viewPageLearnerProfile.currentItem = 2
            })

            showPledgeDialogLiveData.observe(viewLifecycleOwner, Observer {

                it?.apply {

                    if (this) {
                        showPledgeDialog()
                        showPledgeDialogLiveData.value = null
                    } else {
                        pledgeDialog?.dismiss()
                        pledgeDialog = null
                        showPledgeDialogLiveData.value = null
                    }
                }


            })

            navigateToCoursePage.observe(viewLifecycleOwner, Observer {

                it?.apply {

                    if (this) {
                        findNavController()
                            .navigate(R.id.action_learnerProfileFragment_to_coursesFragment)
                        navigateToCoursePage.value = null
                    }
                }
            })
        }

    }

    private fun setupViewPager() {

        val pagerAdapter =
            ViewPagerAdapter(
                childFragmentManager,
                FragmentStatePagerAdapter.POSITION_UNCHANGED
            ).apply {

                addFragmentView(PersonalDetailFragment())
                addFragmentView(AddressDetailsFragment())
                addFragmentView(EducationDetailFragment())
            }

        layoutBinding.viewPageLearnerProfile.apply {
            setSwipeEnabled(false)
            adapter = pagerAdapter
        }

    }

    private fun showDOBPickerDialog() {

        val year: Int
        val month: Int
        val day: Int

        Calendar.getInstance().apply {

            year = get(Calendar.YEAR)
            month = get(Calendar.MONTH)
            day = get(Calendar.DAY_OF_MONTH)
        }

        val datePickerDialog = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { datePicker, sYear, sMonth, sDay ->

                val selectedDate = "$sYear-${sMonth + 1}-$sDay"

                viewModel.registrationModelLiveData?.value?.dateofBirthstring =
                    DateUtils.getDisplayDateFromDate(
                        selectedDate,
                        DateUtils.DATE_PICKER_PATTERN,
                        DateUtils.DATE_DISPLAY_PATTERN_SEP
                    )

                AppLog.infoLog("DAte ::: $year - $sMonth - $sDay")
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = DateTime.now().millis
        datePickerDialog.show()
    }

    private fun showPledgeDialog() {
        pledgeDialog = Dialog(context!!)

        pledgeDialog?.apply {

            val pledgeBinding: PledgeDialogBinding =
                PledgeDialogBinding.inflate(LayoutInflater.from(context))
            setContentView(pledgeBinding.root)
            window?.setDimAmount(0.2F)
            pledgeBinding.learnerProfileViewModel = viewModel
            setCanceledOnTouchOutside(false)

            show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.apply {

                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.profile_changes_discarded),
                        BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                        false,
                        BindingUtils.string(R.string.yes), {
                            isAlertDialogShown.postValue(false)
                            findNavController().navigateUp()
                        },
                        BindingUtils.string(R.string.no), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
