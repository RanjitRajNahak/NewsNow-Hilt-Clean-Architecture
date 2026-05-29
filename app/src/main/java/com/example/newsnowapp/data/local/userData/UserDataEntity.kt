package com.example.app1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
data class User(
    @PrimaryKey val email: String, // Email is unique, perfect for a PrimaryKey
    val name: String,
    val password: String
)