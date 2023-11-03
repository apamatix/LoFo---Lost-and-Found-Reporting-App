package com.example.lostnfound.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lostnfound.database.LostFoundItemDBSchema.ItemTable

class ItemBaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table " + ItemTable.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    ItemTable.Cols.UUID + ", " +
                    ItemTable.Cols.ITEM_PHOTO + ", " +
                    ItemTable.Cols.TITLE + ", " +
                    ItemTable.Cols.DATE + ", " +
                    ItemTable.Cols.TIME + ", " +
                    ItemTable.Cols.LOCATION + ", " +
                    ItemTable.Cols.FOUND + ", " +
                    ItemTable.Cols.COMMENT +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        private const val VERSION = 1
        private const val DATABASE_NAME = "itemBase.db"
    }
}
