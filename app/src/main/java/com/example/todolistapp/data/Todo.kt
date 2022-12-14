package com.example.todolistapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class Todo(
    val title: String,
    val content: String?,
    val isDone: Boolean,
    @PrimaryKey val id: Int? = null
)
