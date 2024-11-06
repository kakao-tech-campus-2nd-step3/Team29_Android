package com.iguana.notetaking.pdf.model

data class AnnotationUIModel(
    val content: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)