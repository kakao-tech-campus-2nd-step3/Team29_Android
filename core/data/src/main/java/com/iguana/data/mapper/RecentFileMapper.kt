package com.iguana.data.mapper

import com.iguana.data.local.entity.RecentFileEntity
import com.iguana.domain.model.RecentFile

// Entity -> Domain Model로 변환
fun RecentFileEntity.toDomainModel(): RecentFile {
    return RecentFile(
        id = this.id,
        fileName = this.fileName,
        fileUri = this.fileUri,
        lastOpened = this.lastOpened,
        bookmarkedPage = this.bookmarkedPage
    )
}

// Domain Model -> Entity로 변환
fun RecentFile.toEntity(): RecentFileEntity {
    return RecentFileEntity(
        id = this.id,
        fileName = this.fileName,
        fileUri = this.fileUri,
        lastOpened = this.lastOpened,
        bookmarkedPage = this.bookmarkedPage
    )
}
