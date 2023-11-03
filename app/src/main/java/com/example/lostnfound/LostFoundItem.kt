package com.example.lostnfound

import java.util.Date
import java.util.UUID

class LostFoundItem {
    var mId: UUID
        private set
    var mTitle: String? = null
    var mDate: Date
    var mTime: Date
    var mItemPhoto: ByteArray? = null
    var mLocation: String? = null
    var mComment: String? = null
    var mFound = false

    constructor() : this(UUID.randomUUID())

    constructor(id: UUID) {
        mId = id
        mDate = Date()
        mTime = Date()
    }

    fun setPhotoData(photoData: ByteArray?) {
        mItemPhoto = photoData
    }
}







