package com.example.qrscanmaster.usecase

import android.provider.CalendarContract.Instances
import androidx.room.Dao
import androidx.room.Room
import android.content.Context
import androidx.room.Database
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.qrscanmaster.model.Barcode


@Database(entities = [Barcode::class], version = 2)
abstract class BarcodeDatabaseFactory : RoomDatabase() {
    abstract fun getBarcodeDatabase(): BarcodeDatabase
}

@Dao
interface BarcodeDatabase {
    companion object {
        private var INSTANCE: BarcodeDatabase? = null

        fun getInstance(context: Context): BarcodeDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                BarcodeDatabaseFactory::class.java,
                "db"
            )
                .addMigrations(object : Migration(1, 2) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        db.execSQL("ALTER TABLE codes ADD COLUMN name TEXT")
                    }


                }).build().getBarcodeDatabase().apply {
                    INSTANCE = this
                }
        }

    }
    @Query("SELECT * FROM codes ORDER BY date DESC")
    fun getAll(): DataSource.Factory<Int, Barcode>

}

