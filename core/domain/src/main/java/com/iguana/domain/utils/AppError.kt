package com.iguana.domain.utils

// 공통적인 에러 클래스
sealed class AppError : Exception() {

    data object FileNotFound : AppError() {
        override val message: String
            get() = "녹음 파일을 찾을 수 없습니다."
    }

    data object UploadFailed : AppError() {
        override val message: String
            get() = "업로드에 실패했습니다."
    }

    data class NetworkError(val errorCode: Int) : AppError() {
        override val message: String
            get() = "네트워크 에러가 발생했습니다. 에러 코드: $errorCode"
    }

    data class UnknownError(val errorMessage: String) : AppError() {
        override val message: String
            get() = "알 수 없는 에러가 발생했습니다: $errorMessage"
    }


    data class NullResponseError(val errorMessage: String) : AppError() {
        override val message: String
            get() = "응답이 비어 있습니다: $errorMessage"
    }

}
