package com.iguana.data.mapper

import com.iguana.data.remote.model.*
import com.iguana.domain.model.Annotation


fun AnnotationResponseDto.toDomain() = Annotation(
    id = id,
    content = content,
    x = x,
    y = y,
    width = width,
    height = height
)

fun GetAnnotationsResponseDto.toDomain() = annotations.map { it.toDomain() }

fun Annotation.toCreateAnnotationRequestDto(pageNumber: Int): CreateAnnotationRequestDto =
    CreateAnnotationRequestDto(
        pageNumber = pageNumber,
        x = x,
        y = y,
        width = width,
        height = height,
        content = content
    )

fun Annotation.toUpdateAnnotationRequestDto(): UpdateAnnotationRequestDto =
    UpdateAnnotationRequestDto(
        content = content,
        x = x,
        y = y,
        width = width,
        height = height
    )