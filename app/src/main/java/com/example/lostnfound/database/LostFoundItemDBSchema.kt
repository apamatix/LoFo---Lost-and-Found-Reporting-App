package com.example.lostnfound.database

class LostFoundItemDBSchema {
    object ItemTable {
        const val NAME = "items"

        object Cols {
            const val UUID = "uuid"
            const val ITEM_PHOTO = "item_photo"
            const val TITLE = "title"
            const val DATE = "date"
            const val TIME = "time"
            const val LOCATION = "location"
            const val COMMENT = "comment"
            const val FOUND = "found"
        }
    }
}