package com.iguana.domain.repository

import com.iguana.domain.model.Annotation

interface AnnotationRepository {
    // 주석 생성
    suspend fun createAnnotation(documentId: Long, annotation: Annotation, pageNumber: Int): Annotation

    // 주석 조회
    suspend fun getAnnotations(documentId: Long, pageNumbers: List<Int>): List<Annotation>

    // 주석 수정
    suspend fun updateAnnotation(documentId: Long, annotation: Annotation): Annotation

    // 주석 삭제
    suspend fun deleteAnnotation(documentId: Long, annotationId: Long)
}
