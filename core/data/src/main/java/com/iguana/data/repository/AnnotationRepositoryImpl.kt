package com.iguana.data.repository

import com.iguana.data.local.dao.AnnotationDao
import com.iguana.data.remote.api.AnnotationApi
import com.iguana.domain.repository.AnnotationRepository
import com.iguana.domain.model.Annotation
import javax.inject.Inject
import com.iguana.data.mapper.toCreateAnnotationRequestDto
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toEntity
import com.iguana.data.mapper.toUpdateAnnotationRequestDto


class AnnotationRepositoryImpl @Inject constructor(
    private val annotationApi: AnnotationApi,
    private val annotationDao: AnnotationDao
) : AnnotationRepository {
    override suspend fun saveAnnotation(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ) {
        // TODO 서버 호출 부분 주석처리 해서 서버 구동되면 주석 해제
        // val requestDto = annotation.toCreateAnnotationRequestDto(pageNumber)
        // val responseDto = annotationApi.createAnnotation(documentId, requestDto)

//        val createdAnnotation = responseDto.toDomain()
          val annotationEntity = annotation.toEntity(documentId, pageNumber)

        // 로컬에 저장
        annotationDao.insertAnnotation(annotationEntity)
        return
    }

    // 특정 페이지 번호로 로컬에서 주석을 조회
    override suspend fun getAnnotationsByPage(documentId: Long, pageNumber: Int): List<Annotation> {
        val localAnnotations = annotationDao.getAnnotationsByPage(documentId, pageNumber)
        return localAnnotations.map { it.toDomain() }
    }

    override suspend fun getAnnotations(documentId: Long, pageNumbers: List<Int>): List<Annotation> {
        // 서버에서 모든 페이지의 주석을 가져옴
        val responseDto = annotationApi.getAnnotations(documentId, pageNumbers)
        val remoteAnnotations = responseDto.annotations.map { it.toDomain() }

        // 로컬 데이터베이스에 각 페이지별로 주석 저장
        remoteAnnotations.forEach { annotation ->
            val pageNumber = annotation.pageNumber  // Annotation에 포함된 페이지 번호 사용
            annotationDao.insertAnnotation(annotation.toEntity(documentId, pageNumber))
        }

        return remoteAnnotations
    }

    override suspend fun updateAnnotation(documentId: Long, annotation: Annotation): Annotation {
        val requestDto = annotation.toUpdateAnnotationRequestDto()
        val responseDto = annotationApi.updateAnnotation(documentId, annotation.id!!, requestDto)
        return responseDto.toDomain()
    }

    override suspend fun deleteAnnotation(documentId: Long, annotationId: Long) {
        annotationApi.deleteAnnotation(documentId, annotationId)
    }

    override suspend fun clearAnnotations() {
        annotationDao.clearAnnotations()
    }

}