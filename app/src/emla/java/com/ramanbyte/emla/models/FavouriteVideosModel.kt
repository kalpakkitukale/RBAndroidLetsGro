package com.ramanbyte.emla.models

import com.ramanbyte.utilities.KEY_BLANK

class FavouriteVideosModel {
    var userId: Int = 0
    var contentId: Int = 0
    var lengthOfVideo: Int = 0
    var contentType: String = KEY_BLANK
    var courseName: String = KEY_BLANK
    var chapterName: String = KEY_BLANK
    var sectionName: String = KEY_BLANK
    var videoLink: String = KEY_BLANK
    var courseId: Int = 0
    var chapterId: Int = 0
    var sectionId: Int = 0
    var status: String = KEY_BLANK
    var deviceId: Int = 0
    var seekPosition: String = KEY_BLANK
    var isLikeVideo: String = KEY_BLANK
    var isFavouriteVideo: String = KEY_BLANK
}