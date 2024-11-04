package com.iguana.domain.repository

import com.iguana.domain.model.Annotation

interface AnnotationRepository {
    // 주석 생성
    suspend fun saveAnnotationInLocal(documentId: Long, annotation: com.iguana.domain.model.Annotation, pageNumber: Int)

    // 서버에 주석 저장
    suspend fun saveAnnotationToServer(documentId: Long, annotation: com.iguana.domain.model.Annotation, pageNumber: Int)

    // 주석 페이지별 조회
    suspend fun getAnnotationsByPage(documentId: Long, pageNumbers: Int): List<Annotation>

    // 주석 조회
    suspend fun getAnnotations(documentId: Long, pageNumbers: List<Int>): List<Annotation>

    // 주석 수정 (서버)
    suspend fun updateAnnotationToServer(documentId: Long, annotation: Annotation): Annotation

    // 주석 수정 (로컬)
    suspend fun updateAnnotationInLocal(annotation: Annotation)

    // 주석 삭제
    suspend fun deleteAnnotation(documentId: Long, annotationId: Long)

    // 테이블 초기화
    suspend fun clearAnnotations()
}
