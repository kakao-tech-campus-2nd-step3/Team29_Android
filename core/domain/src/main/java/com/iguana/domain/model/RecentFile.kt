package com.iguana.domain.model

data class RecentFile(
    val id: String,
    val fileName: String,
    val fileUri: String,
    val lastOpened: Long,
    val bookmarkedPage: Int?
)
