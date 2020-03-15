package com.geoideas.guysafe.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.geoideas.guysafe.model.entity.Zone

@Dao
interface ZoneDao {

    @Insert
    fun insert(zone: Zone);

    @Query("SELECT * FROM zone ORDER BY id DESC LIMIT 1")
    fun read(): Zone

    @Query("DELETE FROM zone")
    fun deleteAll()

}