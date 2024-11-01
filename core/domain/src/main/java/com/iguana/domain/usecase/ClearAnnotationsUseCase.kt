package com.iguana.domain.usecase

import com.iguana.domain.repository.AnnotationRepository
import javax.inject.Inject

class ClearAnnotationsUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend operator fun invoke() {
        annotationRepository.clearAnnotations()
    }
}