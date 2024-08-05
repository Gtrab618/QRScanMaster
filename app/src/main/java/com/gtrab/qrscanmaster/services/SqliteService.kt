package com.gtrab.qrscanmaster.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteService(
    context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        val queryCameraZoom="create table zoom(id INTEGER primary key, percentZoom real, typeCamera INTEGER)"
        db.execSQL(queryCameraZoom)
        /*val queryCodesQr=" CREATE TABLE codes (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,text TEXT NOT NULL, formattedText TEXT NOT NULL, format TEXT NOT NULL,schema TEXT NOT NULL, date INTEGER NOT NULL )"
        db.execSQL(queryCodesQr)*/
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}