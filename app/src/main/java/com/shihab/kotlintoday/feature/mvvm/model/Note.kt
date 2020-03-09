package com.shihab.kotlintoday.feature.mvvm.model

import android.accounts.AuthenticatorDescription
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
class Note(@PrimaryKey(autoGenerate = true) var id: Int, val title: String, val description: String, val priority: Int) {
}