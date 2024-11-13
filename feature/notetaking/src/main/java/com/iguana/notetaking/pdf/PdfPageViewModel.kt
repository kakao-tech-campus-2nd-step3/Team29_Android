package com.iguana.notetaking.pdf


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.usecase.GetAnnotationsByPageUseCase
import com.iguana.domain.usecase.SaveAnnotationUseCase
import com.iguana.domain.usecase.UpdateAnnotationUseCase
import com.iguana.domain.usecase.DeleteAnnotationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfPageViewModel @Inject constructor(
    private val getAnnotationsByPageUseCase: GetAnnotationsByPageUseCase,
    private val saveAnnotationUseCase: SaveAnnotationUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase,
    private val deleteAnnotationUseCase: DeleteAnnotationUseCase
) : ViewModel() {
    private val _annotations = MutableLiveData<List<com.iguana.domain.model.Annotation>>()
    val annotations: LiveData<List<com.iguana.domain.model.Annotation>> get() = _annotations

    fun loadAnnotations(documentId: Long, pageNumber: Int) {
        viewModelScope.launch {
            val annotations = getAnnotationsByPageUseCase(documentId, pageNumber)
            _annotations.postValue(annotations)
        }
    }

    fun saveAnnotation(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ) {
        viewModelScope.launch(Dispatchers.IO)  {
                saveAnnotationUseCase(documentId, annotation, pageNumber)
        }
    }

    fun updateAnnotation(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation
    ) {
        viewModelScope.launch {
            updateAnnotationUseCase(documentId, annotation)
            _annotations.value = _annotations.value.orEmpty().map {
                if (it.id == annotation.id) annotation else it
            }
        }
    }

    fun deleteAnnotation(documentId: Long, annotationId: Long) {
        viewModelScope.launch {
            val isDeleted = deleteAnnotationUseCase(documentId, annotationId)
            if (isDeleted) {
                // 성공적으로 삭제되었을 때만 UI에 반영
                _annotations.postValue(_annotations.value?.filter { it.id != annotationId })
            }
        }
    }
}