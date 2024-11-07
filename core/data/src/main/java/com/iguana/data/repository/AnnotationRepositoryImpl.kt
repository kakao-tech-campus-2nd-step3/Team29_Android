package com.iguana.data.repository

import android.util.Log
import com.iguana.data.local.dao.AnnotationDao
import com.iguana.data.remote.api.AnnotationApi
import com.iguana.domain.repository.AnnotationRepository
import com.iguana.domain.model.Annotation
import javax.inject.Inject
import com.iguana.data.mapper.toCreateAnnotationRequestDto
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toEntity
import com.iguana.data.mapper.toUpdateAnnotationRequestDto
import com.iguana.domain.model.SyncStatus


class AnnotationRepositoryImpl @Inject constructor(
    private val annotationApi: AnnotationApi,
    private val annotationDao: AnnotationDao
) : AnnotationRepository {
    // 로컬에 저장하는 로직
    override suspend fun saveAnnotationInLocal(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ): Long {
        val annotationEntity = annotation.toEntity(documentId, pageNumber)
        Log.d(
            "testt",
            "Saving annotationEntity: x=${annotationEntity.xPosition}, y=${annotationEntity.yPosition}, width=${annotationEntity.width}, height=${annotationEntity.height}"
        )
        return annotationDao.insertAnnotation(annotationEntity)
    }

    // 서버에 저장하는 로직 (현재 주석 처리됨)
    override suspend fun saveAnnotationToServer(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ): Long {
        val requestDto = annotation.toCreateAnnotationRequestDto(pageNumber)
        val responseDto = annotationApi.createAnnotation(documentId, requestDto)
        return responseDto.id
    }

    // 특정 페이지 번호로 로컬에서 주석을 조회
    override suspend fun getAnnotationsByPage(documentId: Long, pageNumber: Int): List<Annotation> {
        val localAnnotations = annotationDao.getAnnotationsByPage(documentId, pageNumber)
        return localAnnotations.map { it.toDomain() }
    }

    override suspend fun getAnnotations(
        documentId: Long,
        pageNumbers: List<Int>
    ): List<Annotation> {
        return try {
            val responseDto = annotationApi.getAnnotations(documentId, pageNumbers)
            val remoteAnnotations = responseDto.toDomain()

            // 로컬 DB에 서버에서 받은 ID로 주석 저장
            remoteAnnotations.forEach { annotation ->
                annotationDao.insertAnnotation(annotation.toEntity(documentId, annotation.pageNumber))
            }

            remoteAnnotations
        } catch (e: Exception) {
            Log.e("AnnotationRepository", "Failed to load annotations: ${e.message}")
            emptyList()
        }
    }

    override suspend fun updateAnnotationToServer(documentId: Long, annotation: com.iguana.domain.model.Annotation): Annotation {
        val requestDto = annotation.toUpdateAnnotationRequestDto()
        val responseDto = annotationApi.updateAnnotation(documentId, annotation.id, requestDto)
        return responseDto.toDomain()
    }


    override suspend fun deleteAnnotation(documentId: Long, annotationId: Long) {
        annotationApi.deleteAnnotation(documentId, annotationId)
    }

    override suspend fun clearAnnotations() {
        annotationDao.clearAnnotations()
    }

    override suspend fun updateSyncStatus(annotationId: Long, syncStatus: SyncStatus) {
        annotationDao.updateSyncStatus(annotationId, syncStatus)
    }

    override suspend fun updateAnnotationInLocal(annotation: com.iguana.domain.model.Annotation) {
        annotationDao.updateAnnotation(
            id = annotation.id,
            content = annotation.content,
            xPosition = annotation.x,
            yPosition = annotation.y,
            width = annotation.width,
            height = annotation.height
        )
    }

    override suspend fun updateAnnotationId(localId: Long, serverId: Long) {
        annotationDao.updateAnnotationId(localId, serverId)
    }

}