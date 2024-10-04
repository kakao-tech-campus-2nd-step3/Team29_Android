package com.iguana.data.repository

import com.iguana.data.remote.api.AnnotationApi
import com.iguana.domain.repository.AnnotationRepository
import com.iguana.domain.model.Annotation
import javax.inject.Inject
import com.iguana.data.mapper.toCreateAnnotationRequestDto
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toUpdateAnnotationRequestDto


class AnnotationRepositoryImpl @Inject constructor(
    private val annotationApi: AnnotationApi
) : AnnotationRepository {
    override suspend fun createAnnotation(
        documentId: Long,
        annotation: Annotation,
        pageNumber: Int
    ): Annotation {
        val requestDto = annotation.toCreateAnnotationRequestDto(pageNumber)
        val responseDto = annotationApi.createAnnotation(documentId, requestDto)
        return responseDto.toDomain()
    }

    override suspend fun getAnnotations(
        documentId: Long,
        pageNumbers: List<Int>
    ): List<Annotation> {
        val responseDto = annotationApi.getAnnotations(documentId, pageNumbers)
        return responseDto.toDomain()
    }

    override suspend fun updateAnnotation(documentId: Long, annotation: Annotation): Annotation {
        val requestDto = annotation.toUpdateAnnotationRequestDto()
        val responseDto = annotationApi.updateAnnotation(documentId, annotation.id!!, requestDto)
        return responseDto.toDomain()
    }

    override suspend fun deleteAnnotation(documentId: Long, annotationId: Long) {
        annotationApi.deleteAnnotation(documentId, annotationId)
    }

}