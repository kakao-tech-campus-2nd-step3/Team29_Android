package com.iguana.domain.usecase

import android.util.Log
import com.iguana.domain.model.SyncStatus
import com.iguana.domain.repository.AnnotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteAnnotationUseCase @Inject constructor(
    private val annotationRepository: AnnotationRepository
) {
    suspend operator fun invoke(documentId: Long, annotationId: Long): Boolean {
        return try {
            // 서버에서 주석 삭제
            withContext(Dispatchers.IO) {
                annotationRepository.deleteAnnotationFromServer(documentId, annotationId)
            }

            // 서버 삭제 성공 시 로컬 DB에서 주석 삭제
            withContext(Dispatchers.IO) {
                annotationRepository.deleteAnnotationFromLocal(annotationId)
            }
            Log.d("DeleteAnnotationUseCase", "Annotation deleted successfully on server and local DB")
            true
        } catch (e: Exception) {
            Log.e("DeleteAnnotationUseCase", "Failed to delete annotation from server: ${e.message}")

            // 서버 삭제 실패 시 로컬의 동기화 상태를 FAILED로 업데이트
            withContext(Dispatchers.IO) {
                annotationRepository.updateSyncStatus(annotationId, SyncStatus.FAILED)
            }
            false
        }
    }
}
