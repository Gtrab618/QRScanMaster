package com.example.qrscanmaster.usecase


import android.content.Context
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qrscanmaster.model.Barcode
import io.reactivex.rxjava3.core.Single


@Database(entities = [Barcode::class], version = 1)
abstract class BarcodeDatabaseFactory : RoomDatabase() {
    abstract fun getBarcodeDatabase(): BarcodeDatabase
}

@Dao
interface BarcodeDatabase {
    companion object {
        private var INSTANCE: BarcodeDatabase? = null
        //revisar qui verifica si la base esta creada si no esta la crea!! cosa que ya esta hecho 02
        fun getInstance(context: Context): BarcodeDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                BarcodeDatabaseFactory::class.java,
                "qr_db_prom"
            ).build().getBarcodeDatabase().apply {
                INSTANCE = this
            }
        }

    }
    @Query("SELECT * FROM  codes ORDER BY date DESC")
    //fun getAll():DataSource<Int,Barcode>
    fun getAll(): DataSource.Factory<Int,Barcode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(barcode: Barcode): Single<Long>
    //revisar la opcion de guardar duplicados que parece incesesaria primeramente 02
    @Query("SELECT * FROM codes WHERE format= :format AND text =:text LIMIT 1")
    fun find(format:String, text:String):Single<List<Barcode>>
}

fun BarcodeDatabase.saveIfNotPresent(barcode:Barcode): Single<Long> {
    // 02 realizar comprobacion de duplicidad
    return find(barcode.format.name,barcode.text)
        .flatMap { found ->
            if (found.isEmpty()){
                save(barcode)
            }else{
                Single.just(found[0].id)
            }

        }
}

