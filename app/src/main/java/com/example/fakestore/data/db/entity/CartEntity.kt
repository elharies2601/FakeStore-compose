package com.example.fakestore.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carts")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val title: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val username: String
)
