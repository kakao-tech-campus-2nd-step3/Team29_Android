package com.iguana.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAnnotationRequestDto(
    val pageNumber: Int,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val content: String
)


@Serializable
data class UpdateAnnotationRequestDto(
    val content: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

@Serializable
data class AnnotationResponseDto(
    val id: Long,
    val documentId: Long,
    val pageNumber: Int,
    val content: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class GetAnnotationsResponseDto(
    val annotations: List<AnnotationResponseDto>
)