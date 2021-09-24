package com.skyyo.template.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.template.application.models.local.Cat
import com.skyyo.template.application.persistance.room.cats.CatsDao
import com.skyyo.template.application.persistance.room.cats.CatsRemoteKeys
import com.skyyo.template.application.persistance.room.cats.CatsRemoteKeysDao

@Database(
    version = 1,
    entities = [
        Cat::class,
        CatsRemoteKeys::class,
    ],
    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
        // when updating just add AutoMigration (from = 2, to = 3) and change the DB version
    ],
)

@TypeConverters(MoshiTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catsDao(): CatsDao
    abstract fun catsRemoteKeysDao(): CatsRemoteKeysDao
}
