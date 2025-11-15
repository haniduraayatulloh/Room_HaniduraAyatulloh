package com.example.praktikum7_225150207111005_haniduraayatulloh

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey (autoGenerate = true)
val id: Int = 0,

val title: String,

val isCompleted: Boolean = false
)