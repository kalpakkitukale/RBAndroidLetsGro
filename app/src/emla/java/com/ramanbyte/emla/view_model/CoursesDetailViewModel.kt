package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.models.CourseSyllabusModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class CoursesDetailViewModel(val mContext: Context) : BaseViewModel(mContext = mContext) {

    private val coursesRepository: CoursesRepository by instance()
    private val masterRepository: MasterRepository by instance()
    var userData = masterRepository?.getCurrentUser()
    var coursesModelLiveData: MutableLiveData<CoursesModel> = MutableLiveData()
    var selectedChaptersModelLiveData: MutableLiveData<ChaptersModel?> = MutableLiveData(null)

    var courseSyllabusModelLiveData = MutableLiveData<CourseSyllabusModel?>(null)

    var showValidationMessage = MutableLiveData<String>(null)

    override var noInternetTryAgain: () -> Unit = {
        getCoursesSyllabus()
    }


    var layoutToShow: MutableLiveData<View> = MutableLiveData()

    fun onHeaderClick(view: View) {
        when (view.id) {
            R.id.headerCourseInfo -> {
                AppLog.infoLog("Course Info Click")
                layoutToShow.value = view
            }
            R.id.headerCourseSyllabus -> {
                AppLog.infoLog("Course Syllabus Click")
                layoutToShow.value = view
            }
            R.id.headerAssessmentInstruction -> {
                AppLog.infoLog("Assessment Instruction Click")
                layoutToShow.value = view
            }
            R.id.headerWhatIWillLearn -> {
                AppLog.infoLog("What i will Learn Click")
                layoutToShow.value = view
            }
            R.id.headerHowToUse -> {
                AppLog.infoLog("How to use Click")
                layoutToShow.value = view
            }
        }
    }

    fun getCoursesSyllabus() {

        CoroutineUtils.main {

            try {

                coroutineToggleLoader(BindingUtils.string(R.string.getting_course_details))

                val courseSyllabus =
                    coursesRepository.getCoursesSyllabus(coursesModelLiveData.value?.courseId ?: 0)

                courseSyllabusModelLiveData.postValue(courseSyllabus)

                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    "",
                    View.GONE
                )

                coroutineToggleLoader()

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)

                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )

                coroutineToggleLoader()

            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.no_internet_message),
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.course_details_unavailable),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }
    }
}