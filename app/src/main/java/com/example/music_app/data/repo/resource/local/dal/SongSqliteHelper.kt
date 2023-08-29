package com.example.music_app.data.repo.resource.local.dal

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.music_app.utils.Constant.CREATE_TABLE
import com.example.music_app.utils.Constant.DB_NAME
import com.example.music_app.utils.Constant.DB_VERSION

class SongSqliteHelper( context: Context?,
name: String?,
factory: SQLiteDatabase.CursorFactory?,
version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = CREATE_TABLE
        db?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        // TODO implement later
    }

    fun queryData(sql: String) {
        val db: SQLiteDatabase = writableDatabase
        db.execSQL(sql)
    }

    fun getData(sql: String): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.rawQuery(sql, null)
    }

    companion object {
        private var instance: SongSqliteHelper? = null
        fun getInstance(context: Context?) = synchronized(this) {
            instance ?: SongSqliteHelper(context, DB_NAME, null, DB_VERSION).also { instance = it }
        }
    }
}