package com.ramanbyte.aws_s3_android.accessor

class AppS3FileInfo(val operationType: Int) {

    companion object {

        const val Download = 0
        const val Upload = 0
    }

    var infoId = 0
    var fileName = ""
    var status = ""
    var progress = 0
    var isFinished = false
}
