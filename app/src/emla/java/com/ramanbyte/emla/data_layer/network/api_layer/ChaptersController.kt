package com.ramanbyte.emla.data_layer.network.api_layer

import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.request.ChapterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChaptersController {

    /*
    * POST getChapterLists
    * */
    @POST("getChapterLists")
    fun getList(@Body chapterRequest: ChapterRequest): Response<ArrayList<ChaptersModel>>

    /*
    * GET GetListOfVideos/{chapterId}
    * */
    @GET("GetListOfVideos/{chapterId}")
    fun getContentList(@Path("chapterId") chapterId: Int): Response<ArrayList<ContentModel>>
}