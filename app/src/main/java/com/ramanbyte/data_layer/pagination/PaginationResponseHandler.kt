package com.ramanbyte.data_layer.pagination


/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 17/10/19
 */
enum class Status {
    INIT_RUNNING,
    INIT_SUCCESS,
    INIT_NO_DATA,
    INIT_FAILED,
    INIT_NO_INTERNET,
    NEXT_RUNNING,
    NEXT_SUCCESS,
    NEXT_NO_DATA,
    NEXT_FAILED,
    NEXT_NO_INTERNET,
}

data class PaginationResponseHandler private constructor(
    val status: Status,
    val msg: String? = null
) {

    companion object {

        val INIT_LOADING = PaginationResponseHandler(Status.INIT_RUNNING)
        val INIT_LOADED = PaginationResponseHandler(Status.INIT_SUCCESS)
        val INIT_NO_DATA = PaginationResponseHandler(Status.INIT_NO_DATA)
        val INIT_ERROR = PaginationResponseHandler(Status.INIT_FAILED)
        val INIT_NO_INTERNET = PaginationResponseHandler(Status.INIT_NO_INTERNET)

        val PAGE_LOADING = PaginationResponseHandler(Status.NEXT_RUNNING)
        val PAGE_LOADED = PaginationResponseHandler(Status.NEXT_SUCCESS)
        val PAGE_NO_DATA = PaginationResponseHandler(Status.NEXT_NO_DATA)
        val PAGE_ERROR = PaginationResponseHandler(Status.NEXT_FAILED)
        val PAGE_NO_INTERNET = PaginationResponseHandler(Status.NEXT_NO_INTERNET)
    }
}