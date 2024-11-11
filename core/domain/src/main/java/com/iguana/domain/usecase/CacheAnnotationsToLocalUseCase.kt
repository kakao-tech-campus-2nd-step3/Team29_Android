package com.iguana.domain.usecase

import android.util.Log
import com.iguana.domain.repository.AnnotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
    주석 캐싱 UseCase
    서버에서 모든 페이지의 주석 데이터를 가져와 로컬에 캐싱
 */

class CacheAnnotationsToLocalUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    // 서버에서 모든 페이지의 주석 데이터를 가져와 로컬에 캐싱
    suspend operator fun invoke(documentId: Long, allPageNumbers: List<Int>) {
        withContext(Dispatchers.IO) {
            val annotations = annotationRepository.getAnnotations(documentId, allPageNumbers)
            Log.d("testt", "annotations: $annotations")
        }
    }
}
