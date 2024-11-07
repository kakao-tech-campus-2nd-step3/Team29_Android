package com.iguana.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
    주석 페이지별로 가져오기 UseCase
    로컬 데이터베이스에서 특정 페이지의 주석을 가져옴
 */

class GetAnnotationsByPageUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    // 로컬 데이터베이스에서 특정 페이지의 주석을 가져옴
    suspend operator fun invoke(documentId: Long, pageNumber: Int): List<com.iguana.domain.model.Annotation> {
        return withContext(Dispatchers.IO) {
            annotationRepository.getAnnotationsByPage(documentId, pageNumber)
        }
    }
}