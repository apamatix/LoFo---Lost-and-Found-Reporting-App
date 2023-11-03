package com.example.lostnfound

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.lostnfound.database.ItemBaseHelper
import com.example.lostnfound.database.ItemCursorWrapper
import com.example.lostnfound.database.LostFoundItemDBSchema.ItemTable
import java.util.*

class LostFoundItemLab private constructor(context: Context) {
    companion object {
        private var sLostFoundItemLab: LostFoundItemLab? = null

        fun get(context: Context): LostFoundItemLab {
            if (sLostFoundItemLab == null) {
                sLostFoundItemLab = LostFoundItemLab(context.applicationContext)
            }
            return sLostFoundItemLab!!
        }
    }

    private val mContext: Context = context.applicationContext
    private val mDatabase: SQLiteDatabase = ItemBaseHelper(mContext).writableDatabase

    fun addItem(i: LostFoundItem) {
        val values = getContentValues(i)
        mDatabase.insert(ItemTable.NAME, null, values)
    }

    fun getItems(): List<LostFoundItem> {
        val items: MutableList<LostFoundItem> = mutableListOf()
        val cursor = queryItems(null, null)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                items.add(cursor.getItem())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return items
    }

    fun getItem(id: UUID): LostFoundItem? {
        val cursor = queryItems(
            "${ItemTable.Cols.UUID} = ?",
            arrayOf(id.toString())
        )
        try {
            if (cursor.count == 0) {
                return null
            }
            cursor.moveToFirst()
            return cursor.getItem()
        } finally {
            cursor.close()
        }
    }
    fun updateItem(item: LostFoundItem) {
        val uuidString = item.mId.toString()
        val values = getContentValues(item)
        mDatabase.update(
            ItemTable.NAME, values,
            ItemTable.Cols.UUID + " = ?",
            arrayOf(uuidString)
        )
    }

    fun removeItem(i: LostFoundItem) {
        val uuidString = i.mId.toString()
        mDatabase.delete(
            ItemTable.NAME,
            ItemTable.Cols.UUID + " = ? ",
            arrayOf(uuidString)
        )
    }

    private fun queryItems(whereClause: String?, whereArgs: Array<String>?): ItemCursorWrapper {
        val cursor: Cursor = mDatabase.query(
            ItemTable.NAME,
            null, // Columns - null selects all columns
            whereClause,
            whereArgs,
            null, // groupBy
            null, // having
            null  // orderBy
        )
        return ItemCursorWrapper(cursor)
    }

    private fun getContentValues(item: LostFoundItem): ContentValues {
        val values = ContentValues()
        values.put(ItemTable.Cols.UUID, item.mId.toString())
        values.put(ItemTable.Cols.ITEM_PHOTO, item.mItemPhoto)
        values.put(ItemTable.Cols.TITLE, item.mTitle)
        values.put(ItemTable.Cols.DATE, item.mDate.time)
        values.put(ItemTable.Cols.TIME, item.mTime.time)
        values.put(ItemTable.Cols.LOCATION, item.mLocation)
        values.put(ItemTable.Cols.FOUND, if (item.mFound) 1 else 0)
        values.put(ItemTable.Cols.COMMENT, item.mComment)

        return values
    }
}

