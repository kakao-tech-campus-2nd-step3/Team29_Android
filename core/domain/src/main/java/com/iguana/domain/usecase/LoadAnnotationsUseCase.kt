package com.iguana.domain.usecase

import android.util.Log
import com.iguana.domain.repository.AnnotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadAnnotationsUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend fun loadAllAnnotations(documentId: Long) {
        withContext(Dispatchers.IO) {
            val allPageNumbers = listOf(1, 2, 3) // 필요한 모든 페이지 번호 목록을 정의
            val result = annotationRepository.getAnnotations(documentId, allPageNumbers)
            Log.d("testt", "(유스케이스) loadAllAnnotations: $result")
           result
        }
    }

    suspend operator fun invoke(documentId: Long, pageNumber: Int): List<com.iguana.domain.model.Annotation> {
        return withContext(Dispatchers.IO) {
            annotationRepository.getAnnotationsByPage(documentId, pageNumber)
        }
    }
}