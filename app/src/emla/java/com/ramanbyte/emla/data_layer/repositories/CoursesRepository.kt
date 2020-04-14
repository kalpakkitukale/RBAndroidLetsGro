package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.CoursesController
import com.ramanbyte.emla.models.CourseSyllabusModel
import org.kodein.di.generic.instance

class CoursesRepository(mContext: Context) :BaseRepository(mContext) {

    val coursesController: CoursesController by instance()

    suspend fun getCoursesSyllabus(courseId: Int): CourseSyllabusModel? {
        return apiRequest {
            coursesController.getCoursesSyllabus(courseId, /*userModel?.userId ?:*/ 0)
        }
    }

}