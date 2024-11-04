package com.iguana.domain.usecase

import com.iguana.domain.repository.AnnotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UpdateAnnotationUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend operator fun invoke(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation
    ) {
        withContext(Dispatchers.IO) {
            annotationRepository.updateAnnotationInLocal(annotation)
            annotationRepository.updateAnnotationToServer(documentId, annotation)
        }
    }
}