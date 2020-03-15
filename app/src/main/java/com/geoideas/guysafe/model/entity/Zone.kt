package com.geoideas.guysafe.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zone")
data class Zone(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long?,
    @ColumnInfo(name = "zones")
    val zones: String
)