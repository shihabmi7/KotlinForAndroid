package com.shihab.kotlintoday.feature.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var title: String,
    var description: String,
    var priority: String
)