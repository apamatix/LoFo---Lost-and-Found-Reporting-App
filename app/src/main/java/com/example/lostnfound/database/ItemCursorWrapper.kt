package com.example.lostnfound.database

import android.database.Cursor
import android.database.CursorWrapper
import com.example.lostnfound.LostFoundItem
import com.example.lostnfound.database.LostFoundItemDBSchema.ItemTable.Cols
import java.util.Date
import java.util.UUID

class ItemCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getItem(): LostFoundItem {
        val uuidString = getString(getColumnIndex(Cols.UUID))
        val title = getString(getColumnIndex(Cols.TITLE))
        val photoData = getBlob(getColumnIndex(Cols.ITEM_PHOTO))
        val date = getLong(getColumnIndex(Cols.DATE))
        val time = getLong(getColumnIndex(Cols.TIME))
        val location = getString(getColumnIndex(Cols.LOCATION))
        val isFound = getInt(getColumnIndex(Cols.FOUND))
        val comment = getString(getColumnIndex(Cols.COMMENT))

        val item = LostFoundItem(UUID.fromString(uuidString))
        item.mTitle = title
        item.mItemPhoto = photoData
        item.mDate = Date(date)
        item.mTime = Date(time)
        item.mLocation = location
        item.mFound = isFound != 0
        item.mComment = comment

        return item
    }
}








