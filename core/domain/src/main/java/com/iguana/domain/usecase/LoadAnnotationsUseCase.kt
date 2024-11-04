package com.iguana.domain.usecase

import com.iguana.domain.repository.AnnotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadAnnotationsUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend operator fun invoke(documentId: Long, pageNumber: Int): List<com.iguana.domain.model.Annotation> {
        return withContext(Dispatchers.IO) {
            annotationRepository.getAnnotationsByPage(documentId, pageNumber)
        }
    }
}