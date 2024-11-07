package com.iguana.data.mapper

import com.iguana.data.local.entity.AnnotationEntity
import com.iguana.data.remote.model.*
import com.iguana.domain.model.Annotation


// 서버의 응답 -> 도메인 모델 (주석 업데이트 시 사용됨)
fun AnnotationResponseDto.toDomain() = Annotation(
    id = id,
    content = content,
    x = x.toFloat(),
    y = y.toFloat(),
    width = width.toFloat(),
    height = height.toFloat(),
    pageNumber = pageNumber
)

// 서버에서 받은 주석 응답들의 리스트 -> 도메인 모델의 리스트 (초기 Fragment 진입 시 서버에 있던 주석들을 로컬에 캐싱 시 사용됨)
fun List<AnnotationResponseDto>.toDomain() = map { it.toDomain() }

// 도메인 모델 -> 서버에 주석 생성 요청을 위한 DTO
fun Annotation.toCreateAnnotationRequestDto(pageNumber: Int): CreateAnnotationRequestDto =
    CreateAnnotationRequestDto(
        pageNumber = pageNumber,
        x = x.toInt(),
        y = y.toInt(),
        width = width.toInt(),
        height = height.toInt(),
        content = content
    )

// 도메인 모델을 로컬 데이터베이스 엔티티로 변환
fun Annotation.toEntity(documentId:Long, pageNumber:Int) = AnnotationEntity(
    id = id,
    documentId = documentId,
    pageNumber = pageNumber,
    content = content,
    xPosition = x,
    yPosition = y,
    width = width,
    height = height
)

fun Annotation.toUpdateAnnotationRequestDto(): UpdateAnnotationRequestDto =
    UpdateAnnotationRequestDto(
        content = content,
        x = x.toInt(),
        y = y.toInt(),
        width = width.toInt(),
        height = height.toInt()
    )

// 로컬 데이터베이스 엔티티를 도메인 모델로 변환
fun AnnotationEntity.toDomain() = Annotation(
    id = id,
    content = content,
    x = xPosition,
    y = yPosition,
    width = width,
    height = height,
    pageNumber = pageNumber
)

// 서버에서 받은 응답 -> 로컬 데이터베이스 엔티티
fun AnnotationResponseDto.toEntity() = AnnotationEntity(
    id = id,
    documentId = documentId,
    pageNumber = pageNumber,
    content = content,
    xPosition = x.toFloat(),
    yPosition = y.toFloat(),
    width = width.toFloat(),
    height = height.toFloat()
)