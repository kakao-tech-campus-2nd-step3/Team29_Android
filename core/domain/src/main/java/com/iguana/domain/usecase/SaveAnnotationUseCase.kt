package com.iguana.domain.usecase

import com.iguana.domain.model.SyncStatus
import com.iguana.domain.repository.AnnotationRepository
import javax.inject.Inject
/*
    주석 저장 UseCase
    주석을 로컬에 저장하고 서버에 동기화 시도
 */

class SaveAnnotationUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend operator fun invoke(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ) {
        // 로컬에 임시 ID로 저장하고, 반환된 ID를 localAnnotationId에 할당
        val localAnnotationId = annotationRepository.saveAnnotationInLocal(
            documentId, annotation.copy(syncStatus = SyncStatus.NOT_SYNCED), pageNumber
        )

        // 서버 동기화 시도
        try {
            val serverAnnotationId = annotationRepository.saveAnnotationToServer(documentId, annotation, pageNumber)

            // 서버 ID로 로컬 ID 업데이트
            annotationRepository.updateAnnotationId(localAnnotationId, serverAnnotationId)
            annotationRepository.updateSyncStatus(serverAnnotationId, SyncStatus.SYNCED)
        } catch (e: Exception) {
            // 동기화 실패 시, 로컬의 상태를 FAILED로 업데이트
            annotationRepository.updateSyncStatus(localAnnotationId, SyncStatus.FAILED)
        }
    }
}