package com.iguana.domain.usecase

import com.iguana.domain.repository.AnnotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveAnnotationUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend operator fun invoke(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ) {
        withContext(Dispatchers.IO) {
            annotationRepository.saveAnnotationInLocal(documentId, annotation, pageNumber)
            annotationRepository.saveAnnotationToServer(documentId, annotation, pageNumber)
        }
    }
}