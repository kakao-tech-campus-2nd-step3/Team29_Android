package com.iguana.notetaking.pdf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.usecase.LoadAnnotationsUseCase
import com.iguana.domain.usecase.SaveAnnotationUseCase
import com.iguana.domain.usecase.UpdateAnnotationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfPageViewModel @Inject constructor(
    private val loadAnnotationsUseCase: LoadAnnotationsUseCase,
    private val saveAnnotationUseCase: SaveAnnotationUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase
) : ViewModel() {
    private val _annotations = MutableLiveData<List<com.iguana.domain.model.Annotation>>()
    val annotations: LiveData<List<com.iguana.domain.model.Annotation>> get() = _annotations

    fun loadAnnotations(documentId: Long, pageNumber: Int) {
        viewModelScope.launch {
            val annotations = loadAnnotationsUseCase(documentId, pageNumber)
            _annotations.postValue(annotations)
        }
    }

    fun saveAnnotation(
        documentId: Long,
        annotation: com.iguana.domain.model.Annotation,
        pageNumber: Int
    ) {
        viewModelScope.launch {
             saveAnnotationUseCase(documentId, annotation, pageNumber)
            _annotations.value = _annotations.value.orEmpty() + annotation
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
}