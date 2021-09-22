package com.skyyo.compose_template.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.compose_template.application.Cat
import com.skyyo.compose_template.application.persistance.room.cats.CatsDao
import com.skyyo.compose_template.application.persistance.room.cats.CatsRemoteKeys
import com.skyyo.compose_template.application.persistance.room.cats.CatsRemoteKeysDao

@Database(
    version = 1,
    entities = [
        Cat::class,
        CatsRemoteKeys::class,
    ],
)

@TypeConverters(MoshiTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catsDao(): CatsDao
    abstract fun catsRemoteKeysDao(): CatsRemoteKeysDao

}
