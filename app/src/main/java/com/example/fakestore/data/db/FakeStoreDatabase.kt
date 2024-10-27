package com.example.fakestore.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakestore.data.db.dao.CartDao
import com.example.fakestore.data.db.entity.CartEntity

@Database(entities = [CartEntity::class], version = 1)
abstract class FakeStoreDatabase: RoomDatabase() {
    abstract fun cartDao(): CartDao
}