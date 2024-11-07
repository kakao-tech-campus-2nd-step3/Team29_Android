package com.iguana.domain.usecase

import android.util.Log
import com.iguana.domain.model.SyncStatus
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
            try {
                // 로컬 DB에서 주석 업데이트 (동기화 상태는 NOT_SYNCED로 설정)
                annotationRepository.updateAnnotationInLocal(
                    annotation.copy(syncStatus = SyncStatus.NOT_SYNCED)
                )

                // 서버에 주석 업데이트 시도
                val updatedAnnotation = annotationRepository.updateAnnotationToServer(documentId, annotation)

                // 서버 업데이트 성공 시 로컬 DB의 동기화 상태를 SYNCED로 업데이트
                annotationRepository.updateSyncStatus(updatedAnnotation.id, SyncStatus.SYNCED)
                Log.d("Annotation", "Update successful and synced with server.")

            } catch (e: Exception) {
                // 서버 업데이트 실패 시, 로컬의 동기화 상태를 FAILED로 설정
                annotationRepository.updateSyncStatus(annotation.id, SyncStatus.FAILED)
                Log.e("Annotation", "Update failed: ${e.message}")
            }
        }
        }
    }