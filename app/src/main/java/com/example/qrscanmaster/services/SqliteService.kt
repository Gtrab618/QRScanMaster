package com.example.qrscanmaster.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteService(
    context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        val queryCameraZoom="create table zoom(id INTEGER primary key, maxZoom real, typeCamera INTEGER)"
        db.execSQL(queryCameraZoom)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}